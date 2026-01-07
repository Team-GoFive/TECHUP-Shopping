package com.kt.domain.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartRequest {

	public record AddItem(

		@NotNull
		UUID productId,

		@Min(1)
		int quantity
	) {}

	public record UpdateQuantity(

		@Min(1)
		int quantity
	) {}
}
