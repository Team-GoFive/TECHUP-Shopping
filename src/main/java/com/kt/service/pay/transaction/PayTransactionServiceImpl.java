package com.kt.service.pay.transaction;

import com.kt.domain.dto.request.PayTransactionRequest;
import com.kt.domain.dto.response.PayTransactionResponse;
import com.kt.repository.pay.transaction.PayTransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayTransactionServiceImpl implements PayTransactionService {


	private final PayTransactionRepository payTransactionRepository;

	@Override
	public Page<PayTransactionResponse.Search> getTransactions(
		UUID userId,
		PayTransactionRequest.Search search,
		Pageable pageable
	) {

		search.validate();
		LocalDate fromDate = search.resolvedFromDate();
		LocalDate toDate = search.resolvedToDate();

		return payTransactionRepository.search(
			userId,
			search.type(),
			fromDate,
			toDate,
			search.keyword(),
			pageable
		);
	}
}
