package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.OrderEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPayUseCase {

	private final OrderService orderService;
	private final PayService payService;

	public void orderPay(String email, List<OrderRequest.Item> items, UUID addressId) {
		orderService.checkStock(items);
		OrderEntity order = orderService.createOrder(email, items, addressId);
		orderService.reduceStock(order.getId());

		payService.processPayment(orderId, amount);
	}
}
