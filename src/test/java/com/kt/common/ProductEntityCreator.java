package com.kt.common;

import java.util.UUID;

import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;

public class ProductEntityCreator {

	public static ProductEntity createProduct(CategoryEntity category) {
		return ProductEntity.create(
			"상품" + UUID.randomUUID(),
			1000L,
			1000L,
			category
		);
	}
}
