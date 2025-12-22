package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RefundStatus {
	REQUESTED("환불요청"),
	COMPLETED("환불완료"),
	REJECTED("환불거부");
	private final String description;
}
