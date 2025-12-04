package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.OrderStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;

public interface OrderService {
	OrderResponse.OrderProducts getOrderProducts(UUID orderId);

	void createOrder(String email, List<OrderRequest.Item> items, UUID addressId);

	void cancelOrder(UUID userId, UUID orderId);

	void updateOrder(UUID userId, UUID orderId, OrderRequest.Update orderRequest);

	Page<AdminOrderResponse.Search> searchOrder(Pageable pageable);

	AdminOrderResponse.Detail getOrderDetail(UUID orderId);

	void updateOrderStatus(UUID orderId, OrderStatus newStatus);

}
