package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.kt.constant.RefundStatus;
import com.kt.domain.entity.RefundHistoryEntity;

public record RefundHistoryResponse(
	UUID refundId,
	UUID orderProductId,
	UUID paymentId,
	Long refundAmount,
	RefundStatus status,
	String requestReason,
	String rejectReason,
	UUID sellerId,
	Instant requestedAt
) {

	public static RefundHistoryResponse from(RefundHistoryEntity refundHistory) {
		return new RefundHistoryResponse(
			refundHistory.getId(),
			refundHistory.getOrderProduct().getId(),
			refundHistory.getPayment().getId(),
			refundHistory.getRefundAmount(),
			refundHistory.getStatus(),
			refundHistory.getRequestReason(),
			refundHistory.getRejectReason(),
			refundHistory.getSellerId(),
			refundHistory.getCreatedAt()
		);
	}
}