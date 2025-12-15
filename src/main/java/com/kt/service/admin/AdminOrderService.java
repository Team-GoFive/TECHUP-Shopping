package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.response.AdminOrderResponse;

public interface AdminOrderService {

	void cancelOrderProduct(UUID userId, UUID orderProductId);

	Page<AdminOrderResponse.Search> searchOrder(Pageable pageable);

	AdminOrderResponse.Detail getOrderDetail(UUID orderId);

	// void updateOrderStatus(UUID orderId, OrderStatus newStatus); // TODO: 정책 수정 필요

}
