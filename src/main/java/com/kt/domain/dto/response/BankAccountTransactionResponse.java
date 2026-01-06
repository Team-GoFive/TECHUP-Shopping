package com.kt.domain.dto.response;

import com.kt.constant.bankaccount.BankAccountTransactionType;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class BankAccountTransactionResponse {

	@Schema(name = "BankAccountTransactionResponse")
	public record Search(
		UUID transactionId,
		BankAccountTransactionType type,
		BigDecimal amount,
		BigDecimal balanceSnapshot,
		String from,
		String to,
		Instant createdAt
	) {
		@QueryProjection
		public Search {}
	}
}
