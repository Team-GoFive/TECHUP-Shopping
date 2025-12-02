package com.kt.service;

import com.kt.config.jwt.JwtTokenProvider;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.TokenType;
import com.kt.constant.UserRole;
import com.kt.constant.mail.MailTemplate;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.LoginRequest;

import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.exception.CustomException;
import com.kt.infra.mail.EmailClient;
import com.kt.infra.redis.RedisCache;

import com.kt.repository.account.AccountRepository;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

import com.kt.util.EncryptUtil;
import com.mysema.commons.lang.Pair;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final CourierRepository courierRepository;
	private final AccountRepository accountRepository;
	private final PasswordRequestRepository passwordRequestRepository;
	private final PasswordEncoder passwordEncoder;

	private final JwtTokenProvider jwtTokenProvider;

	private final RedisCache redisCache;
	private final EmailClient emailClient;

	@Override
	public void signupMember(SignupRequest.SignupMember request) {
		String email = request.email();
		requireVerifiedEmail(email);
		requireDuplicatedEmail(email);

		UserEntity member = UserEntity.create(
			request.name(),
			email,
			passwordEncoder.encode(request.password()),
			UserRole.MEMBER,
			request.gender(),
			request.birth(),
			request.mobile()
		);

		userRepository.save(member);
	}

	@Override
	public void signupCourier(SignupRequest.SignupCourier request) {
		String email = request.email();
		requireVerifiedEmail(email);
		requireDuplicatedEmail(email);

		CourierEntity courier = CourierEntity.create(
			request.name(),
			request.email(),
			request.password(),
			request.gender()
		);

		courierRepository.save(courier);
	}

	@Override
	public Pair<String, String> login(LoginRequest request) {

		AbstractAccountEntity account = accountRepository
			.findByEmailOrThrow(request.email());

		validAccount(account, request.password());

		String accessToken = jwtTokenProvider.create(
			account.getId(),
			account.getEmail(),
			account.getRole(),
			TokenType.ACCESS
		);

		String refreshToken = jwtTokenProvider.create(
			account.getId(),
			account.getEmail(),
			account.getRole(),
			TokenType.REFRESH
		);

		return Pair.of(accessToken, refreshToken);
	}

	@Override
	public void sendAuthCode(SignupRequest.SignupEmail request) {
		String authCode = getAuthenticationCode();
		String email = request.email();
		redisCache.set(RedisKey.SIGNUP_CODE, email, authCode);
		emailClient.sendMail(
			email,
			MailTemplate.VERIFY_EMAIL,
			authCode
		);
	}

	@Override
	public void verifySignupCode(SignupRequest.VerifySignupCode request) {
		String email = request.email();
		if (!redisCache.hasKey(RedisKey.SIGNUP_CODE.key(email))) {
			throw new CustomException(ErrorCode.AUTH_CODE_UNAVAILABLE);
		}

		String redisAuthCode = redisCache.get(
			RedisKey.SIGNUP_CODE.key(email),
			String.class
		);

		if (!redisAuthCode.equals(request.authCode()))
			throw new CustomException(ErrorCode.AUTH_CODE_INVALID);

		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);
	}

	@Override
	public void resetPassword(PasswordManagementRequest.PasswordReset request) {
		String email = request.email();
		AbstractAccountEntity account = accountRepository
			.findByEmailOrThrow(email);
		String reset = getRandomPassword();

		account.resetPassword(passwordEncoder.encode(reset));
		emailClient.sendMail(
			email,
			MailTemplate.RESET_PASSWORD,
			reset
		);
	}

	@Override
	public void requestResetPassword(PasswordManagementRequest.PasswordReset request) {
		AbstractAccountEntity account = accountRepository
			.findByEmailOrThrow(request.email());

		PasswordRequestEntity passwordRequest = passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				account, PasswordRequestStatus.PENDING, PasswordRequestType.RESET
			).orElseGet(() ->
				PasswordRequestEntity.create(
					account, null, PasswordRequestType.RESET
				)
			);

		if (!passwordRequest.isNew())
			throw new CustomException(ErrorCode.PASSWORD_RESET_ALREADY_REQUESTED);

		passwordRequestRepository.save(passwordRequest);
	}

	@Override
	public void requestUpdatePassword(PasswordManagementRequest.PasswordUpdate request) {
		AbstractAccountEntity account = accountRepository
			.findByEmailOrThrow(request.email());

		passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				account,
				PasswordRequestStatus.PENDING,
				PasswordRequestType.UPDATE
			).ifPresent(passwordRequestRepository::delete);

		PasswordRequestEntity passwordRequest = PasswordRequestEntity.create(
			account,
			request.password(),
			PasswordRequestType.UPDATE
		);

		passwordRequestRepository.save(passwordRequest);

	}

	private String getRandomPassword() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}

	private void validAccount(AbstractAccountEntity account, String rawPassword) {
		if (!passwordEncoder.matches(rawPassword, account.getPassword()))
			throw new CustomException(ErrorCode.AUTH_FAILED_LOGIN);

		switch (account.getStatus()) {
			case DELETED -> throw new CustomException(ErrorCode.AUTH_ACCOUNT_DELETED);
			case DISABLED -> throw new CustomException(ErrorCode.AUTH_ACCOUNT_DISABLED);
			case RETIRED -> throw new CustomException(ErrorCode.AUTH_ACCOUNT_RETIRED);
		}
	}

	private void requireDuplicatedEmail(String email) {
		if (accountRepository.findByEmail(email).isPresent())
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
	}

	private void requireVerifiedEmail(String email) {
		Boolean result = redisCache.get(
			RedisKey.SIGNUP_VERIFIED.key(email),
			Boolean.class
		);
		if (!Boolean.TRUE.equals(result))
			throw new CustomException(ErrorCode.AUTH_EMAIL_UNVERIFIED);
	}

	private String getAuthenticationCode() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}

}
