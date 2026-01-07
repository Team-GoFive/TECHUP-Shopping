package com.kt.domain.entity;

import com.kt.constant.PaymentStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentEntity extends BaseEntity {

	@Column(nullable = false)
	private Long totalProductPrice;

	@Column(nullable = false)
	private Long deliveryPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@OneToOne
	@JoinColumn(name = "order_product_id", nullable = false)
	private OrderProductEntity orderProduct;

	private PaymentEntity(
		Long totalProductPrice,
		Long deliveryPrice,
		OrderProductEntity orderProduct
	) {
		this.totalProductPrice = totalProductPrice;
		this.deliveryPrice = deliveryPrice;
		this.paymentStatus = PaymentStatus.PAID;
		this.orderProduct = orderProduct;
	}

	public static PaymentEntity create(
		final Long totalProductPrice,
		final Long deliveryPrice,
		final OrderProductEntity orderProduct
	) {
		return new PaymentEntity(totalProductPrice, deliveryPrice, orderProduct);
	}

	public long getRefundAmount() {
		return totalProductPrice + deliveryPrice;
	}

	public void refund() {
		if (paymentStatus != PaymentStatus.PAID) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}
		this.paymentStatus = PaymentStatus.REFUND_COMPLETED;
	}

	public void cancel() {
		if (paymentStatus != PaymentStatus.PAID) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}
		this.paymentStatus = PaymentStatus.CANCELED;
	}
}
