package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderSourceType;
import com.kt.constant.PaymentStatus;
import com.kt.constant.ShippingType;
import com.kt.constant.AccountRole;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ShippingDetailEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.AddressRepository;
import com.kt.repository.PayRepository;
import com.kt.repository.PaymentRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.ShippingDetailRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final ShippingDetailRepository shippingDetailRepository;
	private final AddressRepository addressRepository;
	private final PaymentRepository paymentRepository;
	private final PayRepository payRepository;


	@Override
	public OrderResponse.OrderProducts getOrderProducts(UUID orderId) {
		List<OrderProductEntity> orderProducts = orderProductRepository.findWithProductByOrderId(orderId);
		return OrderResponse.OrderProducts.from(orderId, orderProducts);
	}

	// TODO: @Lock 붙이기
	public void checkStock(List<OrderRequest.Item> items) {
		for (OrderRequest.Item item : items) {
			ProductEntity product = productRepository.findByIdOrThrow(item.productId());

			if (product.getStock() < item.quantity()) {
				throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
			}
		}
	}

	@Override
	public OrderEntity createOrder(
		UUID userId,
		OrderRequest request,
		OrderSourceType sourceType)
	{
		List<OrderRequest.Item> items = request.items();
		UUID addressId = request.addressId();

		checkStock(items);

		UserEntity user = userRepository.findByIdOrThrow(userId);

		AddressEntity address = addressRepository.findByIdAndCreatedByOrThrow(addressId, user);

		ReceiverVO receiverVO = ReceiverVO.create(
			address.getReceiverName(),
			address.getReceiverMobile(),
			address.getCity(),
			address.getDistrict(),
			address.getRoadAddress(),
			address.getDetail()
		);

		OrderEntity order = OrderEntity.create(receiverVO, user);
		orderRepository.save(order);

		for (OrderRequest.Item item : items) {
			ProductEntity product =
				productRepository.findByIdOrThrow(item.productId());

			OrderProductEntity orderProduct = new OrderProductEntity(
				item.quantity(),
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			);

			order.addOrderProduct(orderProduct);
			orderProductRepository.save(orderProduct);
		}
		return order;
	}

	@Transactional
	public void reduceStock(UUID orderId) {
		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(orderId);

		for (OrderProductEntity orderProduct : orderProducts) {
			ProductEntity product = orderProduct.getProduct();
			Long quantity = orderProduct.getQuantity();

			product.decreaseStock(quantity);
			orderProduct.updateStatus(OrderProductStatus.PENDING_APPROVE);
		}
	}

	@Override
	@Transactional
	public void cancelOrderProduct(UUID userId, UUID orderProductId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);

		OrderEntity order = orderProduct.getOrder();
		hasOrderCancelPermission(user, order);

		if (orderProduct.getStatus() == OrderProductStatus.SHIPPING
			|| orderProduct.getStatus() == OrderProductStatus.SHIPPING_COMPLETED) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
		}

		if (!orderProduct.isCancelable()) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CONFIRMED);
		}

		ProductEntity product = orderProduct.getProduct();
		product.addStock(orderProduct.getQuantity());

		PaymentEntity payment = paymentRepository.findByOrderProduct(orderProduct)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (payment.getPaymentStatus() == PaymentStatus.REFUND_COMPLETED) {
			throw new CustomException(ErrorCode.ALREADY_REFUNDED);
		}

		PayEntity pay = payRepository.findByUser(user)
			.orElseThrow(() -> new CustomException(ErrorCode.PAY_NOT_FOUND));

		long amount = payment.getRefundAmount();

		pay.refund(amount);
		payment.cancel();
		orderProduct.cancel();
	}

	// TODO: for seller
	@Override
	public void changeOrderAddress(UUID userId, UUID orderId, OrderRequest.Update request) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		OrderEntity order = orderRepository.findByIdOrThrow(orderId);

		hasOrderUpdatePermission(user, order);

		List<OrderProductEntity> orderProducts = orderProductRepository.findAllByOrderId(orderId);
		List<ShippingDetailEntity> shippingDetails = shippingDetailRepository.findAllByOrderProductIn(orderProducts);

		boolean shippingStarted = shippingDetails.stream()
			.anyMatch(shippingDetail -> shippingDetail.getShippingType() != ShippingType.READY);

		if (shippingStarted) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
		}

		ReceiverVO newReceiverVO = ReceiverVO.create(
			request.receiverName(),
			request.receiverMobile(),
			request.city(),
			request.district(),
			request.roadAddress(),
			request.detail()
		);

		order.updateReceiverVO(newReceiverVO);
	}

	@Override
	public Page<OrderResponse.Search> searchOrder(Pageable pageable) {
		return orderRepository.findAll(pageable)
			.map(OrderResponse.Search::from);
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse.Detail getOrderDetail(UUID orderId) {

		OrderEntity order = orderRepository.findDetailWithProducts(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		return OrderResponse.Detail.from(
			order,
			order.getOrderProducts()
		);
	}

	private void hasOrderCancelPermission(UserEntity user, OrderEntity order) {
		AccountRole role = order.getOrderBy().getRole();
		UUID orderId = order.getOrderBy().getId();
		UUID userId = user.getId();

		if (role != AccountRole.ADMIN && !orderId.equals(userId)) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_NOT_ALLOWED);
		}
	}

	private void hasOrderUpdatePermission(UserEntity user, OrderEntity order) {
		UUID orderId = order.getOrderBy().getId();
		UUID userId = user.getId();

		if (!orderId.equals(userId)) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_NOT_ALLOWED);
		}
	}
}