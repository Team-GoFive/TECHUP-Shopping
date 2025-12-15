package com.kt.service.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderStatus;
import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

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

	private void hasOrderCancelPermission(UserEntity user, OrderEntity order) {
		UserRole role = order.getOrderBy().getRole();
		UUID orderId = order.getOrderBy().getId();
		UUID userId = user.getId();

		if (role != UserRole.ADMIN && !orderId.equals(userId)) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_NOT_ALLOWED);
		}
	}

	private boolean isCancelable(OrderStatus status) {
		if (status == OrderStatus.PURCHASE_CONFIRMED)
			return false;
		if (status == OrderStatus.SHIPPING_COMPLETED)
			return false;
		return true;
	}
}
