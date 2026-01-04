package com.kt.service.user;

import com.kt.constant.AccountRole;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSignupService {

	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final RedisCache redisCache;
	private final PasswordEncoder passwordEncoder;
	private final BankAccountRepository bankAccountRepository;

	@Transactional
	public void signupUser(SignupRequest.SignupUser signup) {
		validateSignupEmailVerified(signup.email());
		validateEmailNotDuplicated(signup.email());
		UserEntity user = UserEntity.create(
			signup.name(),
			signup.email(),
			passwordEncoder.encode(signup.password()),
			AccountRole.MEMBER,
			signup.gender(),
			signup.birth(),
			signup.mobile()
		);

		userRepository.save(user);

		BankAccountEntity bankAccount = BankAccountEntity.create(user);
		bankAccountRepository.save(bankAccount);
	}

	private void validateSignupEmailVerified(String email) {
		Boolean result = redisCache.get(
			RedisKey.SIGNUP_VERIFIED.key(email),
			Boolean.class
		);
		if (!Boolean.TRUE.equals(result))
			throw new CustomException(ErrorCode.AUTH_EMAIL_UNVERIFIED);
	}

	private void validateEmailNotDuplicated(String email) {
		if (accountRepository.findByEmail(email).isPresent())
			throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
	}
}
