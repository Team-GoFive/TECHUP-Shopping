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
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.ShippingDetailEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.OrderRepository;
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

	@Override
	public OrderResponse.OrderProducts getOrderProducts(UUID orderId) {
		List<OrderProductEntity> orderProducts = orderProductRepository.findAllByOrderId(orderId);
		return OrderResponse.OrderProducts.of(orderId, orderProducts);
	}

	@Override
	public void createOrder(String email, List<OrderRequest.Item> items) {

		UserEntity user = userRepository.findByEmailOrThrow(email);

		// TODO: 컨트롤러 구현
		ReceiverVO receiverVO = ReceiverVO.create(
			"홍길동",
			"010-1234-5678",
			"서울특별시",
			"강남구",
			"테헤란로",
			"111호"
		);

		OrderEntity order = OrderEntity.create(receiverVO, user);
		orderRepository.save(order);

		for (OrderRequest.Item item : items) {

			UUID productId = item.productId();
			Long quantity = item.quantity();

			ProductEntity product = productRepository.findByIdOrThrow(productId);

			if (product.getStock() < quantity) {
				throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
			}

			product.decreaseStock(quantity);

			OrderProductEntity orderProduct = new OrderProductEntity(
				quantity,
				product.getPrice(),
				OrderProductStatus.CREATED,
				order,
				product
			);

			orderProductRepository.save(orderProduct);
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
	public void cancelOrder(UUID orderId) {
		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

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

	@Override
	public void updateOrder(UUID orderId, OrderRequest.Update request) {
		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

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

		OrderEntity order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		List<OrderProductEntity> orderProducts =
			orderProductRepository.findAllByOrderId(orderId);

		return AdminOrderResponse.Detail.from(order, orderProducts);
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

}