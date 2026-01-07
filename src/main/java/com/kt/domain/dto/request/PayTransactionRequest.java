package com.kt.domain.dto.request;

import com.kt.constant.pay.PayTransactionType;

import com.kt.domain.dto.request.common.BaseTransactionPeriodResolver;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public class PayTransactionRequest {

	@Schema(name = "PayTransactionSearch")
	public record Search(
		PayTransactionType type,
		LocalDate fromDate,
		LocalDate toDate,
		String keyword
	) {

		static final int PAY_TRANSACTION_DEFAULT_MONTH = 1;

		public void validate() {
			BaseTransactionPeriodResolver.validatePeriod(fromDate, toDate);
		}

		public LocalDate resolvedFromDate() {
			return BaseTransactionPeriodResolver.resolveFromDate(fromDate, toDate, PAY_TRANSACTION_DEFAULT_MONTH);
		}

		public LocalDate resolvedToDate() {
			return BaseTransactionPeriodResolver.resolveToDate(fromDate, toDate);
		}
	}
}
