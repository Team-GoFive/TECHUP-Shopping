package com.kt.service.admin;

import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.order.OrderRepository;
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
		orderProduct.cancel();
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

	// TODO: 관리자 취소 정책 수정 필요
	//  @Override
	// public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
	// 	OrderEntity order = orderRepository.findByIdOrThrow(orderId);
	//
	// 	OrderStatus current = order.getStatus();
	//
	// 	if (current == OrderStatus.PURCHASE_CONFIRMED) {
	// 		throw new CustomException(ErrorCode.ORDER_ALREADY_CONFIRMED);
	// 	}
	//
	// 	if (current == OrderStatus.SHIPPING) {
	// 		throw new CustomException(ErrorCode.ORDER_ALREADY_SHIPPED);
	// 	}
	//
	// 	order.updateStatus(newStatus);
	// }

	private void hasOrderCancelPermission(UserEntity user, OrderEntity order) {
		AccountRole role = order.getOrderBy().getRole();
		UUID orderId = order.getOrderBy().getId();
		UUID userId = user.getId();

		if (role != AccountRole.ADMIN && !orderId.equals(userId)) {
			throw new CustomException(ErrorCode.ORDER_ACCESS_NOT_ALLOWED);
		}
	}

}
