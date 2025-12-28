package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.kt.constant.RefundStatus;

public record RefundQueryResponse(
	UUID refundId,
	UUID orderProductId,
	UUID paymentId,
	Long refundAmount,
	RefundStatus status,
	String requestReason,
	String rejectReason,
	UUID sellerId,
	Instant createdAt
)
{
}
