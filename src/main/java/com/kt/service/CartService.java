package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.response.CartResponse;
import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.CartItemEntity;

public interface CartService {

	CartResponse.Cart getCartView(UUID userId);

	CartEntity getCart(UUID userId);

	void addItem(UUID userId, UUID productId, int quantity);

	void changeQuantity(UUID userId, UUID cartItemId, int quantity);

	void removeItem(UUID userId, UUID cartItemId);

	void clear(UUID userId);

	List<CartItemEntity> getCartItemsForOrder(
		UUID userId,
		List<UUID> cartItemIds
	);

	void removeOrderedItems(List<CartItemEntity> orderedItems);

}
