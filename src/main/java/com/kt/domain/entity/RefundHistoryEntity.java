package com.kt.domain.entity;

import java.util.UUID;

import com.kt.constant.RefundStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundHistoryEntity extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne
	private PaymentEntity payment;

	@ManyToOne
	private OrderProductEntity orderProduct;

	private Long refundAmount;

	private String requestReason;
	private String rejectReason;

	@Enumerated(EnumType.STRING)
	private RefundStatus status;

	private UUID sellerId;

	private RefundHistoryEntity(
		PaymentEntity payment,
		OrderProductEntity orderProduct,
		long refundAmount,
		String requestReason
	) {
		if (refundAmount <= 0) {
			throw new CustomException(ErrorCode.INVALID_REFUND_AMOUNT);
		}
		if (requestReason == null || requestReason.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_REFUND_REASON);
		}
		this.payment = payment;
		this.orderProduct = orderProduct;
		this.refundAmount = refundAmount;
		this.requestReason = requestReason;
		this.status = RefundStatus.REQUESTED;
	}

	public static RefundHistoryEntity request(
		PaymentEntity payment,
		OrderProductEntity orderProduct,
		long refundAmount,
		String requestReason
	) {
		return new RefundHistoryEntity(
			payment,
			orderProduct,
			refundAmount,
			requestReason
		);
	}

	public void complete(UUID sellerId) {
		if (this.status != RefundStatus.REQUESTED) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}
		this.status = RefundStatus.COMPLETED;
		this.sellerId = sellerId;
	}

	public void reject(UUID sellerId, String reason) {
		if (this.status != RefundStatus.REQUESTED) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}
		this.status = RefundStatus.REJECTED;
		this.sellerId = sellerId;
		this.rejectReason = reason;
	}

}
