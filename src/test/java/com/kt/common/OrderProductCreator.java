package com.kt.common;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;

import com.kt.domain.entity.SellerEntity;

public class OrderProductCreator {
	public static OrderProductEntity createOrderProduct(
		OrderEntity order,
		ProductEntity product,
		SellerEntity seller
	) {
		return OrderProductEntity.create(
			5L,
			5000L,
			OrderProductStatus.CREATED,
			order,
			product
		);
	}
}
