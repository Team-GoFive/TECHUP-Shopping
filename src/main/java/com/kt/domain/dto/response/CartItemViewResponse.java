package com.kt.domain.dto.response;

import java.util.UUID;

public record CartItemViewResponse(
	UUID cartItemId,
	UUID productId,
	String productName,
	long price,
	int quantity,
	boolean isPurchasable
) {
}
