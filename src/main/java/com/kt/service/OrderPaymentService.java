package com.kt.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.domain.dto.request.OrderRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPaymentService {

	private final OrderService orderService;
	// TODO: PayService 주입

	public void orderPay(UUID userId, OrderRequest request) {
		orderService.reduceStock(request.items());
		orderService.createOrder(userId, request);
		// TODO: PayService 결제 메서드 호출
	}
}
