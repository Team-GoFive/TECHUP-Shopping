package com.kt.domain.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SellerProductRequest {
	public record Update(
		@NotBlank(message = "상품 이름은 필수 항목입니다.")
		String name,
		@NotNull(message = "상품 가격은 필수 항목입니다.")
		Long price,
		@NotNull(message = "상품 재고는 필수 항목입니다.")
		Long stock,
		@NotNull(message = "카테고리 ID는 필수 항목입니다.")
		UUID categoryId
	) {
	}

	public record Create(
		@NotBlank(message = "상품 이름은 필수 항목입니다.")
		String name,
		@NotNull(message = "상품 가격은 필수 항목입니다.")
		Long price,
		@NotNull(message = "상품 재고는 필수 항목입니다.")
		Long stock,
		@NotNull(message = "카테고리 ID는 필수 항목입니다.")
		UUID categoryId
	) {
	}

	public record SoldOut(
		@NotNull(message = "상품 ID 목록은 필수 항목입니다.")
		List<UUID> productIds
	) {
	}

	public record Activate(
		@NotNull(message = "상품 ID 목록은 필수 항목입니다.")
		List<UUID> productIds
	) {
	}

	public record InActivate(
		@NotNull(message = "상품 ID 목록은 필수 항목입니다.")
		List<UUID> productIds
	) {
	}
}
