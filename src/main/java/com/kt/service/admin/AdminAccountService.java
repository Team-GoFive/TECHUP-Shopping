package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.PasswordRequestResponse;

public interface AdminAccountService {
	Page<AccountResponse.Search> searchAccounts(
		AccountRequest.Search request,
		Pageable pageable
	);

	void resetAccountPassword(UUID passwordRequestId);

	void updateAccountPassword(UUID passwordRequestId);

	void updatePassword(
		UUID accountId,
		String currentPassword,
		String newPassword
	);

	void deleteAccount(UUID accountId);

	void deleteAccountPermanently(UUID accountId);

	Page<PasswordRequestResponse.Search> searchPasswordRequests(
		PasswordRequest.Search request,
		Pageable pageable
	);

}
