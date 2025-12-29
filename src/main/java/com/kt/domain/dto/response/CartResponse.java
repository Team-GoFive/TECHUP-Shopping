package com.kt.domain.dto.response;

import java.util.List;

public class CartResponse {

	public record Cart(

		List<CartItemViewResponse> items,
		long totalAmount
	) {
		public static Cart from(List<CartItemViewResponse> items) {
			long totalAmount = items.stream()
				.mapToLong(item -> item.price() * item.quantity())
				.sum();

			return new Cart(items, totalAmount);
		}
	}
}