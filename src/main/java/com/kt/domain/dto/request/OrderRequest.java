package com.kt.domain.dto.request;

import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.CartItemEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "OrderRequest")
public record OrderRequest(
	@NotNull
	List<Item> items,

	@NotNull
	UUID addressId
) {
	@Schema(name = "OrderRequestItem")
	public record Item(
		@NotNull
		UUID productId,

		@NotNull
		@Min(1)
		Long quantity,

		@NotNull
		UUID sellerId
	) {
	}

	@Schema(name = "OrderUpdateRequest")
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

	@Schema(name = "OrderChangeStatusRequest")
	public record ChangeStatus(
		@NotNull
		OrderProductStatus status
	) {
	}

	@Schema(name = "CartOrderRequest")
	public record CartOrderRequest(
		@NotNull
		List<UUID> cartItemIds,

		@NotNull
		UUID addressId
	) {
	}

	public static OrderRequest fromCart(
		List<CartItemEntity> cartItems,
		UUID addressId
	) {
		List<Item> items = cartItems.stream()
			.map(ci -> new Item(
				ci.getProduct().getId(),
				Long.valueOf(ci.getQuantity()),
				ci.getProduct().getSeller().getId()
			))
			.toList();

		return new OrderRequest(items, addressId);
	}

}