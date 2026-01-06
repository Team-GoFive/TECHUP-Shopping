package com.kt.domain.dto.request;

import com.kt.constant.SortDirection;
import com.kt.constant.bankaccount.BankAccountTransactionType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BankAccountTransactionRequest {

	@Schema(name = "BankAccountTransactionSearch")
	public record Search(
		BankAccountTransactionType type,
		LocalDate fromDate,
		LocalDate toDate,
		SortDirection sort,
		String keyword
	) {

		public LocalDate resolvedFromDate() {
			if (fromDate == null)
				return LocalDate.now().minusMonths(3);
			return fromDate;
		}

		public LocalDate resolvedToDate() {
			return toDate != null ? toDate : LocalDate.now();
		}

		public void validatePeriod() {
			if (resolvedFromDate().isAfter(resolvedToDate())) {
				throw new IllegalArgumentException("조회 시작일은 종료일보다 이후일 수 없습니다.");
			}
			if (ChronoUnit.YEARS.between(resolvedFromDate(), resolvedToDate()) > 1) {
				throw new IllegalArgumentException("조회 기간은 최대 1년까지 가능합니다.");
			}
		}
	}
}
