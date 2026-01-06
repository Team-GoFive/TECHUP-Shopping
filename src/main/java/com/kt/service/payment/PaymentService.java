package com.kt.service.payment;

import com.kt.domain.entity.OrderProductEntity;

public interface PaymentService {

	void create(
		Long totalPrice, Long deliveryPrice, OrderProductEntity orderProduct
	);
}
