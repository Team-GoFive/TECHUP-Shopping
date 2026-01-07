package com.kt.service.pay.transaction;

import com.kt.domain.dto.request.PayTransactionRequest;
import com.kt.domain.dto.response.PayTransactionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PayTransactionService {

	Page<PayTransactionResponse.Search> getTransactions(
		UUID userId,
		PayTransactionRequest.Search search,
		Pageable pageable
	);
}
