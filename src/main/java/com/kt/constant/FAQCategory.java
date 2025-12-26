package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FAQCategory {
	ACCOUNT("계정"),
	ORDER("주문"),
	DELIVERY("배송"),
	RETURN("반품"),
	PAYMENT("결제"),
	PRODUCT("상품"),
	OTHER("기타"),
	;

	private final String description;
}
