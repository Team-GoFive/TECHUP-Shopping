package com.kt.domain.dto.response;

import java.util.List;
import java.util.UUID;

import com.kt.constant.ProductStatus;
import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.CartItemEntity;
import com.kt.domain.entity.ProductEntity;

public class CartResponse {

	public record Cart(

		List<Item> items,
		long totalAmount
	) {}

	public record Item(

		UUID cartItemId,
		UUID productId,
		String productName,
		long price,
		int quantity,
		boolean isPurchasable
	) {}

	public static Cart toResponse(CartEntity cart) {
		List<Item> items = cart.getItems().stream()
			.map(CartResponse::toItem)
			.toList();

		long totalAmount = items.stream()
			.mapToLong(item -> item.price() * item.quantity())
			.sum();

		return new Cart(items, totalAmount);
	}

	private static Item toItem(CartItemEntity cartItem) {
		ProductEntity product = cartItem.getProduct();

		boolean isPurchasable =
			product.getStatus() == ProductStatus.ACTIVATED;

		return new Item(
			cartItem.getId(),
			product.getId(),
			product.getName(),
			product.getPrice(),
			cartItem.getQuantity(),
			isPurchasable
		);
	}
}
