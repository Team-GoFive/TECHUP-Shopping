package com.kt.domain.dto.request;

import com.kt.constant.OrderProductStatus;

import com.kt.domain.entity.CartItemEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class OrderRequest {

	@Schema(name = "OrderCreateRequest")
	public record Create(

		@NotNull
		List<Item> items,

		@NotNull(message = "주소는 필수 항목입니다.")
		UUID addressId
	) {
	}

	@Schema(name = "OrderRequestItem")
	public record Item(
		@NotNull(message = "상품은 필수 항목입니다.")
		UUID productId,

		@NotNull(message = "주문 상품의 수량은 필수 항목입니다.")
		@Min(value = 1, message = "주문 상품 수량은 0보다 커야합니다.")
		Long quantity
	) {
	}

	@Schema(name = "OrderUpdateRequest")
	public record Update(

		@NotBlank
		String receiverName,

		@NotBlank
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

	public record CartOrder(
		@NotNull
		List<UUID> cartItemIds,

		@NotNull(message = "주소는 필수 항목압니다.")
		UUID addressId
	) {
	}

	public static OrderRequest.Create fromCart(
		List<CartItemEntity> cartItems,
		UUID addressId
	) {
		List<OrderRequest.Item> items = cartItems.stream()
			.map(ci -> new OrderRequest.Item(
				ci.getProduct().getId(),
				Long.valueOf(ci.getQuantity())
			))
			.toList();

		return new OrderRequest.Create(items, addressId);
	}

}
