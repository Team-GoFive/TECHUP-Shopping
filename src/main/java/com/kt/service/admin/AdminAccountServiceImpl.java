package com.kt.service.admin;

import java.util.Random;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.mail.MailTemplate;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.PasswordRequestResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.exception.CustomException;
import com.kt.infra.mail.EmailClient;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.util.EncryptUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {
	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepository;
	private final PasswordRequestRepository passwordRequestRepository;
	private final EmailClient emailClient;

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

		String encodedPassword = passwordEncoder.encode(newPassword);
		account.updatePassword(encodedPassword);
	}

	@Override
	public void deleteAccount(UUID accountId) {
		AbstractAccountEntity account = accountRepository.findByIdOrThrow(accountId);
		account.delete();
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

		passwordRequest.updateStatus(PasswordRequestStatus.COMPLETED);

		emailClient.sendMail(
			account.getEmail(),
			MailTemplate.RESET_PASSWORD,
			resetPassword
		);

	}

	@Override
	public void updateAccountPassword(UUID passwordRequestId) {
		PasswordRequestEntity passwordRequest = passwordRequestRepository.findByIdOrThrow(
			passwordRequestId, PasswordRequestType.UPDATE
		);

		if (passwordRequest.getRequestType() != PasswordRequestType.UPDATE)
			throw new CustomException(ErrorCode.PASSWORD_UPDATE_REQUESTS_NOT_FOUND);

		AbstractAccountEntity account = passwordRequest.getAccount();

		String requestedDecryptPassword = EncryptUtil.decrypt(
			passwordRequest.getEncryptedPassword()
		);

		account.updatePassword(
			passwordEncoder.encode(requestedDecryptPassword)
		);

		passwordRequest.updateStatus(PasswordRequestStatus.COMPLETED);
		passwordRequest.clearEncryptedPassword();

		emailClient.sendMail(
			account.getEmail(),
			MailTemplate.UPDATE_PASSWORD,
			requestedDecryptPassword
		);

	}

	@Override
	public Page<PasswordRequestResponse.Search> searchPasswordRequests(
		PasswordRequest.Search request,
		Pageable pageable
	) {
		return accountRepository.searchPasswordRequests(request, pageable);
	}

	private String getRandomPassword() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}
}
