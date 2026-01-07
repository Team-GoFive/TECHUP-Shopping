package com.kt.domain.dto.request.common;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public final class BaseTransactionPeriodResolver {

	public static LocalDate resolveFromDate(
		LocalDate fromDate,
		LocalDate toDate,
		int defaultMonths
	) {
		if (fromDate == null && toDate == null) {
			return LocalDate.now().minusMonths(defaultMonths);
		}
		return fromDate;
	}

	public static LocalDate resolveToDate(
		LocalDate fromDate,
		LocalDate toDate
	) {
		if (fromDate == null && toDate == null) {
			return LocalDate.now();
		}
		return toDate;
	}

	public static void validatePeriod(
		LocalDate fromDate,
		LocalDate toDate
	) {
		if ((fromDate == null) != (toDate == null)) {
			throw new IllegalArgumentException("조회 시작일과 종료일은 함께 전달되어야 합니다.");
		}

		if (fromDate == null) return;

		if (fromDate.isAfter(toDate)) {
			throw new IllegalArgumentException("조회 시작일은 종료일보다 이후일 수 없습니다.");
		}

		if (ChronoUnit.DAYS.between(fromDate, toDate) > 365) {
			throw new IllegalArgumentException("조회 기간은 최대 1년까지 가능합니다.");
		}
	}
}
