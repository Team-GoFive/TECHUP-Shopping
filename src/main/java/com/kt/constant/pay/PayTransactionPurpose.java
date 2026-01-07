package com.kt.constant.pay;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PayTransactionPurpose {

	CHARGE("페이충전"),
	WITHDRAW("페이인출"),
	ORDER_PAYMENT("주문결제"),
	ORDER_REFUND("주문환불 입금");

	private final String description;
}
