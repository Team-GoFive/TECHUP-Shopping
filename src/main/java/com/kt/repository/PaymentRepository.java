package com.kt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.PaymentEntity;
import com.kt.exception.CustomException;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	Optional<PaymentEntity> findByOrderProduct(OrderProductEntity orderProduct);

	default PaymentEntity findByOrderProductOrThrow(OrderProductEntity orderProduct) {
		return findByOrderProduct(orderProduct)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
	}
}
