package com.kt.repository.pay.transaction;

import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.dto.response.PayTransactionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface PayTransactionRepositoryCustom {

	Page<PayTransactionResponse.Search> search(
		UUID userId,
		PayTransactionType type,
		LocalDate fromDate,
		LocalDate toDate,
		String keyword,
		Pageable pageable
	);
}
