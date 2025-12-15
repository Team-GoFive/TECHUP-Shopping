package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.OrderStatus;
import com.kt.domain.dto.response.AdminOrderResponse;

public interface AdminOrderService {

	void cancelOrder(UUID userId, UUID orderId);

	Page<AdminOrderResponse.Search> searchOrder(Pageable pageable);

	AdminOrderResponse.Detail getOrderDetail(UUID orderId);

	void updateOrderStatus(UUID orderId, OrderStatus newStatus);

}
