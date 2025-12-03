package com.kt.service;

import java.util.UUID;

import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.entity.AbstractAccountEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
	Page<AccountResponse.Search> searchAccounts(
		AccountRequest.Search request,
		Pageable pageable
	);

	void adminResetAccountPassword(UUID accountId);

	void updatePassword(
		UUID accountId,
		String currentPassword,
		String newPassword
	);

	void deleteAccount(UUID accountId);

	AccountResponse.CourierDetail getCourierDetail(UUID courierId);

	AccountResponse.UserDetail getUserDetail(UUID userId);

	void deleteAccountPermanently(UUID accountId);

}
