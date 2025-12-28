package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {

	PENDING("결제대기중"),
	PAID("결제완료"),
	FAILED("결제실패"),
	CANCELED("배송전취소"),          // 배송 전 취소
	REFUND_COMPLETED("환불완료");    // 배송 후 환불

	private final String description;
}
