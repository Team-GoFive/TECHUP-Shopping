package com.kt.service.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderProductStatusPolicy;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.exception.CustomException;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

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
	@Transactional
	public void forceChangeStatus(
		UUID orderProductId,
		OrderProductStatus status
	) {
		OrderProductEntity orderProduct =
			orderProductRepository.findByIdOrThrow(orderProductId);

		OrderProductStatus currentStatus = orderProduct.getStatus();

		if (currentStatus == status) {
			throw new CustomException(ErrorCode.SAME_STATUS_CHANGE_NOT_ALLOWED);
		}

		if (!OrderProductStatusPolicy.canForceChange(currentStatus, status)) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}

		orderProduct.forceChangeStatus(status);
	}

}
