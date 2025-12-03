package com.kt.service;

import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;

import com.kt.constant.OrderStatus;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.dto.request.OrderRequest;

public interface OrderService {
	OrderResponse.OrderProducts getOrderProducts(UUID orderId);
	void createOrder(String email, List<OrderRequest.Item> items, UUID addressId);
	void cancelOrder(UUID orderId);
	void updateOrder(UUID orderId, OrderRequest.Update orderRequest);

	Page<AdminOrderResponse.Search> searchOrder(Pageable pageable);
	AdminOrderResponse.Detail getOrderDetail(UUID orderId);
	void updateOrderStatus(UUID orderId, OrderStatus newStatus);



}
