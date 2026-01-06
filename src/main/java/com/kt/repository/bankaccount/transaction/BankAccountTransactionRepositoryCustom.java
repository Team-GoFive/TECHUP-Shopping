package com.kt.repository.bankaccount.transaction;

import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.dto.response.BankAccountTransactionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BankAccountTransactionRepositoryCustom {

	Page<BankAccountTransactionResponse.Search> search(
		UUID holderId,
		BankAccountTransactionRequest.Search condition,
		Pageable pageable
	);
}
