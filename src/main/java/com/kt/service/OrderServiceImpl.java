package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderStatus;
import com.kt.constant.ShippingType;
import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ShippingDetailEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.AddressRepository;
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

	@Override
	public OrderResponse.OrderProducts getOrderProducts(UUID orderId) {
		List<OrderProductEntity> orderProducts = orderProductRepository.findWithProductByOrderId(orderId);
		return OrderResponse.OrderProducts.of(orderId, orderProducts);
	}

	// TODO: @Lock 붙이기
	@Transactional(readOnly = true)
	public void checkStock(List<OrderRequest.Item> items) {
		for (OrderRequest.Item item : items) {
			ProductEntity product = productRepository.findByIdOrThrow(item.productId());

			if (product.getStock() < item.quantity()) {
				throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
			}
		}
	}

	@Override
	public OrderEntity createOrder(String email, List<OrderRequest.Item> items, UUID addressId) {

		UserEntity user = userRepository.findByEmailOrThrow(email);

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

			UUID productId = item.productId();
			Long quantity = item.quantity();

			ProductEntity product = productRepository.findByIdOrThrow(productId);

			OrderProductEntity orderProduct = new OrderProductEntity(
				quantity,
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

		OrderEntity order = orderRepository.findByIdOrThrow(orderId);
		
		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(orderId);
		
		for (OrderProductEntity orderProduct : orderProducts) {
			ProductEntity product = orderProduct.getProduct();
			Long quantity = orderProduct.getQuantity();

			product.decreaseStock(quantity);
			orderProduct.updateStatus(OrderProductStatus.PAID);
		}
	}

	private boolean isCancelable(OrderStatus status) {
		if (status == OrderStatus.PURCHASE_CONFIRMED)
			return false;
		if (status == OrderStatus.SHIPPING_COMPLETED)
			return false;
		return true;
	}

	@Override
	public void cancelOrder(UUID userId, UUID orderId) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		OrderEntity order = orderRepository.findByIdOrThrow(orderId);

		hasOrderCancelPermission(user, order);

		if (!isCancelable(order.getStatus())) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CONFIRMED);
		}

		List<OrderProductEntity> orderProducts = orderProductRepository.findAllByOrderId(orderId);

		for (OrderProductEntity orderproduct : orderProducts) {
			ProductEntity product = orderproduct.getProduct();
			product.addStock(orderproduct.getQuantity());
			orderproduct.cancel();
		}
		order.cancel();
	}

	// TODO: for seller
	@Override
	public void updateOrder(UUID userId, UUID orderId, OrderRequest.Update request) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		OrderEntity order = orderRepository.findByIdOrThrow(orderId);

		hasOrderUpdatePermission(user, order);

		if (order.getStatus() == OrderStatus.PURCHASE_CONFIRMED) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CONFIRMED);
		}

		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(orderId);

		List<ShippingDetailEntity> shippingDetails =
			shippingDetailRepository.findAllByOrderProductIn(orderProducts);

		boolean shippingStarted = shippingDetails.stream()
			.anyMatch(sd -> sd.getShippingType() != ShippingType.READY);

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
		orderRepository.save(order);
	}

	@Override
	public Page<AdminOrderResponse.Search> searchOrder(Pageable pageable) {
		return orderRepository.findAll(pageable)
			.map(AdminOrderResponse.Search::from);
	}

	@Override
	public AdminOrderResponse.Detail getOrderDetail(UUID orderId) {

		OrderEntity order = orderRepository.findDetailWithProducts(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		return AdminOrderResponse.Detail.from(order, order.getOrderProducts());
	}

	@Override
	public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
		OrderEntity order = orderRepository.findByIdOrThrow(orderId);

		OrderStatus current = order.getStatus();

		if (current == OrderStatus.PURCHASE_CONFIRMED) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_CONFIRMED);
		}

		if (current == OrderStatus.SHIPPING) {
			throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
		}

		order.updateStatus(newStatus);
	}

	private void hasOrderCancelPermission(UserEntity user, OrderEntity order) {
		UserRole role = order.getOrderBy().getRole();
		UUID orderId = order.getOrderBy().getId();
		UUID userId = user.getId();

		if (role != UserRole.ADMIN && !orderId.equals(userId)) {
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