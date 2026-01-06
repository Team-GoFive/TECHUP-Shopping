package com.kt.service.bankaccount.transaction;

import com.kt.domain.dto.request.BankAccountTransactionRequest;

import com.kt.domain.dto.response.BankAccountTransactionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BankAccountTransactionService {

	Page<BankAccountTransactionResponse.Search> getTransactions(
		UUID holderId,
		BankAccountTransactionRequest.Search search,
		Pageable pageable
	);
}
