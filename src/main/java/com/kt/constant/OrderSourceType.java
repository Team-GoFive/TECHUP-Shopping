package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderSourceType {
	DIRECT("바로결제"),
	CART("장바구니에서결제")
	;

	private final String description;
}
