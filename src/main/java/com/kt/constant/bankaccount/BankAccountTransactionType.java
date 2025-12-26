package com.kt.constant.bankaccount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankAccountTransactionType {
	DEPOSIT("입금"),
	WITHDRAW("출금");

	private final String description;
}
