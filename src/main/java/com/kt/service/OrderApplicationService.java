package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderSourceType;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.CartItemEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderApplicationService {
	private final CartService cartService;
	private final OrderService orderService;

	public void createOrderFromCart(
		UUID userId,
		OrderRequest.CartOrderRequest request
	) {
		List<CartItemEntity> cartItems =
			cartService.getCartItemsForOrder(userId, request.cartItemIds());

		OrderRequest orderRequest =
			OrderRequest.fromCart(cartItems, request.addressId());

		orderService.createOrder(
			userId,
			orderRequest,
			OrderSourceType.CART
		);

		cartService.removeOrderedItems(cartItems);
	}
}
