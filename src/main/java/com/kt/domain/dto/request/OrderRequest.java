package com.kt.domain.dto.request;

import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
	@NotNull
	List<Item> items,

	@NotNull
	UUID addressId
) {
	public record Item(
		@NotNull
		UUID productId,

		@NotNull
		@Min(1)
		Long quantity
	) {
	}

	public record Update(
		@NotBlank
		String receiverName,

		String receiverMobile,

		@NotBlank
		String city,

		@NotBlank
		String district,

		@NotBlank
		String roadAddress,

		String detail
	) {
	}

	public record ChangeStatus(
		@NotNull
		OrderStatus status
	) {
	}
}