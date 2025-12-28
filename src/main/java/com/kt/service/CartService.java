package com.kt.service;

import java.util.UUID;

import com.kt.domain.entity.CartEntity;

public interface CartService {
	CartEntity getCart(UUID userId);
	void addItem(UUID userId, UUID productId, int quantity);
	void changeQuantity(UUID userId, UUID cartItemId, int quantity);
	void removeItem(UUID userId, UUID cartItemId);
	void clear(UUID userId);
}
