package com.kt.service.account;

import java.util.UUID;

public interface AccountService {

	void updatePassword(
		UUID accountId,
		String currentPassword,
		String newPassword
	);

	void deleteAccount(UUID accountId);

	void deleteAccountPermanently(UUID accountId);

}
