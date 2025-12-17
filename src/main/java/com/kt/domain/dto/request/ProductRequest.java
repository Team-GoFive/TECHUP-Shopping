package com.kt.domain.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRequest {
	public record Create(
		@NotBlank(message = "상품 이름은 필수 항목입니다.")
		String name,
		@NotNull(message = "상품 가격은 필수 항목입니다.")
		@Min(value = 0, message = "상품 가격은 최소 0원이어야 합니다.")
		Long price,
		@NotNull(message = "상품 재고는 필수 항목입니다.")
		@Positive(message = "상품 재고는 0보다 작을 수 없습니다.")
		Long stock,
		@NotNull(message = "카테고리 ID는 필수 항목입니다.")
		UUID categoryId,
		@NotNull(message = "판매자 ID는 필수 항목입니다.")
		UUID sellerId
	) {
	}

	public record Update(
		@NotBlank(message = "상품 이름은 필수 항목입니다.")
		String name,
		@NotNull(message = "상품 가격은 필수 항목입니다.")
		@Min(value = 0, message = "상품 가격은 최소 0원이어야 합니다.")
		Long price,
		@NotNull(message = "상품 재고는 필수 항목입니다.")
		@Positive(message = "상품 재고는 0보다 작을 수 없습니다.")
		Long stock,
		@NotNull(message = "카테고리 ID는 필수 항목입니다.")
		UUID categoryId
	) {
	}
}
