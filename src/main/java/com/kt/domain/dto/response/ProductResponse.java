package com.kt.domain.dto.response;

import java.util.UUID;

import com.kt.constant.ProductStatus;
import com.kt.domain.entity.ProductEntity;
import com.querydsl.core.annotations.QueryProjection;

public class ProductResponse {
	public record Search(
		UUID id,
		String name,
		Long price,
		ProductStatus status,
		UUID categoryId,
		Long stock
	) {
		@QueryProjection
		public Search {
		}
	}

	public record Detail(
		UUID id,
		String name,
		Long price,
		ProductStatus status,
		UUID categoryId,
		Long stock
	) {

		public static Detail from(ProductEntity product) {
			return new Detail(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getStatus(),
				product.getCategory().getId(),
				product.getStock()
			);
		}
	}
}
