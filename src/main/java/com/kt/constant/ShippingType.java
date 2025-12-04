package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ShippingType {
	READY("배송 준비"),
	DELIVERING("배송"),
	RETURNING("반품");

	private final String description;
}
