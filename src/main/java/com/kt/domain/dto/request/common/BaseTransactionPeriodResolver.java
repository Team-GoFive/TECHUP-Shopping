package com.kt.domain.dto.request.common;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public final class BaseTransactionPeriodResolver {

	static final int YEAR_DAYS = 365;

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
			throw new CustomException(ErrorCode.INVALID_SEARCH_PERIOD_PAIR);
		}

		if (fromDate == null) return;

		if (fromDate.isAfter(toDate)) {
			throw new CustomException(ErrorCode.INVALID_SEARCH_PERIOD_ORDER);
		}

		if (ChronoUnit.DAYS.between(fromDate, toDate) > YEAR_DAYS) {
			throw new CustomException(ErrorCode.INVALID_SEARCH_PERIOD_RANGE);
		}

	}
}
