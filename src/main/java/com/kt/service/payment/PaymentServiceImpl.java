package com.kt.service.payment;

import com.kt.domain.entity.OrderProductEntity;

import com.kt.domain.entity.PaymentEntity;
import com.kt.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	@Override
	@Transactional
	public void create(Long totalPrice, Long deliveryPrice, OrderProductEntity orderProduct) {
		PaymentEntity payment = PaymentEntity.create(
			totalPrice,
			deliveryPrice,
			orderProduct
		);

		paymentRepository.save(payment);
	}
}
