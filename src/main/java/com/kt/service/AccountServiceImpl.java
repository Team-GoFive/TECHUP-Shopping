package com.kt.service;

import java.util.UUID;

import com.kt.constant.mail.MailTemplate;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.entity.AbstractAccountEntity;

import com.kt.infra.mail.EmailClient;
import com.kt.repository.account.AccountRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final UserRepository userRepository;
	private final CourierRepository courierRepository;
	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepository;
	private final EmailClient emailClient;

	@Override
	public Page<AccountResponse.Search> searchAccounts(
		AccountRequest.Search request,
		Pageable pageable
	) {
		return accountRepository.searchAccounts(request, pageable);
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
	public void deleteAccount(UUID accountId) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);
		account.delete();
	}

	@Override
	public void deleteAccountPermanently(UUID accountId) {
		accountRepository.deleteById(accountId);
	}

	@Override
	public void adminResetAccountPassword(UUID accountId) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);
		String resetPassword = getRandomPassword();
		account.resetPassword(passwordEncoder.encode(resetPassword));
		emailClient.sendMail(
			account.getEmail(),
			MailTemplate.RESET_PASSWORD,
			resetPassword
		);
	}

	@Override
	public AbstractAccountEntity getAccountDetail(UUID accountId) {
		return accountRepository.findByIdOrThrow(accountId);
	}

	private String getRandomPassword() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}
}
