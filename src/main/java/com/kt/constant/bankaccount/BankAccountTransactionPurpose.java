package com.kt.constant.bankaccount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankAccountTransactionPurpose {
	SALARY("급여입금"),
	PAY_CHARGE("페이충전"),
	PAY_WITHDRAW("페이잔액 계좌 입금"),
	ORDER_SETTLEMENT("판매정산 입금"),
	ORDER_REFUND("주문환불 출금"),
	MEMBERSHIP("맴버십 출금");

	private final String description;
}
