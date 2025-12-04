package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {

	PENDING("결제대기중"),
	PAID("결제완료"),
	FAILED("결제실패"),
	APPLY_REFUND("환불신청"),
	COMPLETE_REFUND("환불완료");

	private final String description;
}
