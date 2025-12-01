package com.kt.repository.account;

import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.response.AccountResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountRepositoryCustom {
	Page<AccountResponse.Search> searchAccounts(
		AccountRequest.Search request,
		Pageable pageable
	);
}
