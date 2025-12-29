package com.kt.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.domain.dto.request.OrderRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderPayUseCase {

	private final OrderService orderService;
	// TODO: PayService 주입

	public void orderPay(UUID userId, OrderRequest request) {
		orderService.reduceStock(request.items()); // 1. 재고 차감
		orderService.createOrder(userId, request); // 2. 주문 생성
		// TODO: PayService 결제 메서드 호출 // 3. 결제
	}
}
