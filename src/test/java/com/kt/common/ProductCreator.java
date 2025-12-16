package com.kt.common;

import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;

import com.kt.domain.entity.SellerEntity;

public class ProductCreator {
	public static ProductEntity createProduct(CategoryEntity category, SellerEntity seller){
		return ProductEntity.create(
			"테스트상품명",
			1000L,
			5L,
			category,
			seller
		);
	}
}
