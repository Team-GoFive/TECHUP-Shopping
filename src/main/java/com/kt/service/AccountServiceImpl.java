package com.kt.service;

import java.util.UUID;
import com.kt.domain.entity.AbstractAccountEntity;

import com.kt.repository.account.AccountRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepository;

	@Override
	public void deleteAccount(UUID accountId) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);
		account.delete();
	}

	@Override
	public void updatePassword(
		UUID accountId,
		String currentPassword,
		String newPassword
	) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);

		if (!passwordEncoder.matches(currentPassword, account.getPassword()))
			throw new CustomException(ErrorCode.INVALID_PASSWORD);

		if (passwordEncoder.matches(newPassword, account.getPassword()))
			throw new CustomException(ErrorCode.PASSWORD_UNCHANGED);

		String hashedPassword = passwordEncoder.encode(newPassword);
		account.updatePassword(hashedPassword);
	}

	@Override
	public void deleteAccountPermanently(UUID accountId) {
		accountRepository.deleteById(accountId);
	}


}
