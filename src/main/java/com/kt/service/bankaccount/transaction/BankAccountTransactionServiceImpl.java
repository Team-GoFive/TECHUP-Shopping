package com.kt.service.bankaccount.transaction;

import com.kt.domain.dto.request.BankAccountTransactionRequest;

import com.kt.domain.dto.response.BankAccountTransactionResponse;

import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountTransactionServiceImpl implements BankAccountTransactionService {

	private final BankAccountTransactionRepository bankAccountTransactionRepository;

	@Override
	public Page<BankAccountTransactionResponse.Search> getTransactions(
		UUID holderId,
		BankAccountTransactionRequest.Search search,
		Pageable pageable
	) {
		return bankAccountTransactionRepository.search(
			holderId,
			search,
			pageable
		);
	}
}
