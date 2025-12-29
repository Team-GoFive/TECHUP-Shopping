package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.domain.entity.OrderEntity;

public interface OrderService {
	OrderResponse.OrderProducts getOrderProducts(UUID orderId);

	void checkStock(List<OrderRequest.Item> items);

	OrderEntity createOrder(
		UUID userId,
		OrderRequest request
	);

	void reduceStock(UUID orderId);

	void cancelOrderProduct(UUID userId, UUID orderProductId);

	void changeOrderAddress(UUID userId, UUID orderId, OrderRequest.Update orderRequest);

	Page<OrderResponse.Search> searchOrder(Pageable pageable);

	OrderResponse.Detail getOrderDetail(UUID orderId);
}
