package com.kt.service;

import com.kt.common.SignupUserRequestCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.user.UserSignupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.kt.common.SignupUserRequestCreator.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class UserSignupServiceTest {

	@Autowired
	UserSignupService userSignupService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Autowired
	RedisCache redisCache;

	final static String SIGNUP_EMAIL = "user@test.com";

	@BeforeEach
	void setup() {
		redisCache.set(RedisKey.SIGNUP_VERIFIED, SIGNUP_EMAIL, true);
	}

	@Test
	void 유저_회원가입_성공() {
		SignupRequest.SignupUser signupRequest =
			SignupUserRequestCreator.createSignupUserRequest(SIGNUP_EMAIL);

		userSignupService.signupUser(signupRequest);

		UserEntity user = userRepository.findByEmail(SIGNUP_EMAIL).orElse(null);

		assertNotNull(user.getId());
		assertNotNull(user.getPay().getId());

		BankAccountEntity bankAccount = bankAccountRepository.findByHolderId(user.getId()).orElse(null);
		assertNotNull(bankAccount);
	}

	@Test
	void 유저_회원가입_실패_인증정보_없음_시간초과() throws InterruptedException {
		// given
		String email = "member@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED.key(email), true, Duration.ofMillis(100));
		Thread.sleep(200);

		// when
		SignupRequest.SignupUser request = createSignupUserRequest(email);

		// then
		assertThrowsExactly(
			CustomException.class,
			() -> userSignupService.signupUser(request),
			ErrorCode.AUTH_EMAIL_UNVERIFIED.getMessage()
		);
	}

	@Test
	void 유저_회원가입_실패_인증정보_없음_이메일_키값() {
		// when and then
		SignupRequest.SignupUser request = createSignupUserRequest();

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> userSignupService.signupUser(request)
		);
		assertEquals(ErrorCode.AUTH_EMAIL_UNVERIFIED, exception.error());
	}

	@Test
	@Transactional
	void 유저_회원가입_실패_email_중복() {
		// given
		UserEntity user = UserEntityCreator.create();
		userRepository.save(user);

		// when and then
		SignupRequest.SignupUser signupRequest = createSignupUserRequest(SIGNUP_EMAIL);

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> userSignupService.signupUser(signupRequest)
		);
		assertEquals(ErrorCode.DUPLICATED_EMAIL, exception.error());
	}
}
