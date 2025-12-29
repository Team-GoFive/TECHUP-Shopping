package com.kt.constant.pay;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PayTransactionType {

	CREDIT("잔액증가"),
	DEBIT("잔액감소");

	private final String description;

}
