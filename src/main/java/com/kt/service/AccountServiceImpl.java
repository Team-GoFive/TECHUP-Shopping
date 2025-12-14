package com.kt.service;

import java.util.UUID;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.mail.MailTemplate;
import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.PasswordRequestResponse;
import com.kt.domain.entity.AbstractAccountEntity;

import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.infra.mail.EmailClient;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.account.AccountRepository;

import com.kt.util.EncryptUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepository;
	private final PasswordRequestRepository passwordRequestRepository;
	private final EmailClient emailClient;

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
	public Page<AccountResponse.Search> searchAccounts(
		AccountRequest.Search request,
		Pageable pageable
	) {
		return accountRepository.searchAccounts(request, pageable);
	}

	@Override
	public void deleteAccountPermanently(UUID accountId) {
		accountRepository.deleteById(accountId);
	}

	@Override
	public void resetAccountPassword(UUID passwordRequestId) {
		PasswordRequestEntity passwordRequest = passwordRequestRepository.findByIdOrThrow(
			passwordRequestId, PasswordRequestType.RESET
		);

		if (passwordRequest.getRequestType() != PasswordRequestType.RESET)
			throw new CustomException(ErrorCode.BAD_REQUEST);

		AbstractAccountEntity account = passwordRequest.getAccount();

		String resetPassword = getRandomPassword();
		account.resetPassword(passwordEncoder.encode(resetPassword));
		passwordRequest.updateStatus(
			PasswordRequestStatus.COMPLETED
		);

		emailClient.sendMail(
			account.getEmail(),
			MailTemplate.RESET_PASSWORD,
			resetPassword
		);

	}

	@Override
	public void updateAccountPassword(UUID accountId) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);
		PasswordRequestEntity passwordRequest = getPendingPasswordRequest(
			account,
			PasswordRequestType.UPDATE
		);

		String decryptPassword = EncryptUtil.decrypt(passwordRequest.getEncryptedPassword());
		passwordRequest.updateStatus(
			PasswordRequestStatus.COMPLETED
		);
		account.updatePassword(
			passwordEncoder.encode(decryptPassword)
		);
		passwordRequest.clearEncryptedPassword();
		emailClient.sendMail(
			account.getEmail(),
			MailTemplate.UPDATE_PASSWORD,
			decryptPassword
		);

	}

	@Override
	public Page<PasswordRequestResponse.Search> searchPasswordRequests(
		PasswordRequest.Search request,
		Pageable pageable
	) {
		return accountRepository.searchPasswordRequests(request, pageable);
	}

	private PasswordRequestEntity getPendingPasswordRequest(
		AbstractAccountEntity requiredAccount,
		PasswordRequestType requestType
	) {

		ErrorCode errorCode = requestType == PasswordRequestType.UPDATE ?
			ErrorCode.PASSWORD_UPDATE_REQUESTS_NOT_FOUND :
			ErrorCode.PASSWORD_RESET_REQUESTS_NOT_FOUND;

		return passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				requiredAccount,
				PasswordRequestStatus.PENDING,
				requestType
			).orElseThrow(
				() -> new CustomException(errorCode)
			);
	}

	private String getRandomPassword() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}
}
