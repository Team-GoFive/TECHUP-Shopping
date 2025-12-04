package com.kt.common;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;

public class OrderProductCreator {
	public static OrderProductEntity createOrderProduct(
		OrderEntity order,
		ProductEntity product
	){
		return new OrderProductEntity(
			5L,
			5000L,
			OrderProductStatus.CREATED,
			order,
			product
		);
	}
}
