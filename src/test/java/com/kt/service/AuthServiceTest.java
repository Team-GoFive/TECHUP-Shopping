package com.kt.service;

import com.kt.constant.Gender;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.ResetPasswordRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.exception.CustomException;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

import com.mysema.commons.lang.Pair;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class AuthServiceTest {

	@Autowired
	AuthServiceImpl authService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	CourierRepository courierRepository;
	@Autowired
	PasswordRequestRepository passwordRequestRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	RedisCache redisCache;
	@Autowired
	RedisTemplate redisTemplate;
	UserEntity user;
	String rawPassword = "1231231!";
	String email = "bjwnstkdbj@naver.com";

	@BeforeEach
	void setUp(TestInfo testInfo) {
		userRepository.deleteAll();
		accountRepository.deleteAll();
		var connection = redisTemplate.getConnectionFactory().getConnection();
		connection.flushAll();
		saveMember();
	}

	void saveMember() {
		user = UserEntity.create(
			"황테스트",
			email,
			passwordEncoder.encode(rawPassword),
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(2000, 11, 11),
			"010-1234-5678"
		);
		userRepository.save(user);
		UserEntity savedUser = userRepository.findById(user.getId()).orElse(null);
		assertNotNull(savedUser);
	}

	@Test
	void 맴버_회원가입_성공_테스트() {
		String email = "test@email.com";
		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);

		SignupRequest.SignupMember signup = new SignupRequest.SignupMember(
			"황테스터",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.of(2011, 11, 11),
			"010-1234-1234"
		);
		authService.signupMember(signup);
		UserEntity member = userRepository.findByEmail(signup.email()).orElseGet(
			() -> null
		);
		assertNotNull(member);
	}
	// 해야함 인증 정보 null 이거나 false 일때 에러 에러

	@Test
	void 맴버_회원가입_실패_인증정보_없음_시간초과() throws InterruptedException {
		String email = "test@email.com";
		redisCache.set(
			RedisKey.SIGNUP_VERIFIED.key(email),
			true,
			Duration.ofSeconds(2)
		);

		Thread.sleep(2500);

		SignupRequest.SignupMember signup = new SignupRequest.SignupMember(
			"황테스터1",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.of(2011, 11, 10),
			"010-1234-0001"
		);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.signupMember(signup)
		);
	}

	@Test
	void 맴버_회원가입_실패_인증정보_없음_이메일_키값() {
		String email = "test@email.com";
		String differentEmail = "test_different@email.com";
		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);

		SignupRequest.SignupMember signup = new SignupRequest.SignupMember(
			"황테스터1",
			differentEmail,
			"1231231!",
			Gender.MALE,
			LocalDate.of(2011, 11, 10),
			"010-1234-0001"
		);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.signupMember(signup)
		);
	}

	@Test
	void 맴버_회원가입_실패_email_중복() {
		String email = "test@email.com";
		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);
		SignupRequest.SignupMember firstSignup = new SignupRequest.SignupMember(
			"황테스터1",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.of(2011, 11, 10),
			"010-1234-0001"
		);

		SignupRequest.SignupMember secondSignup = new SignupRequest.SignupMember(
			"황테스터2",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.of(2011, 11, 11),
			"010-1234-0002"
		);
		authService.signupMember(firstSignup);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.signupMember(secondSignup)
		);

	}

	@Test
	void 유저_로그인_성공() {
		LoginRequest login = new LoginRequest(
			user.getEmail(),
			rawPassword
		);

		Pair<String, String> result = authService.login(login);

		assertNotNull(result.getFirst());
		assertNotNull(result.getSecond());
		log.info("access token : {}", result.getFirst());
		log.info("refresh token : {}", result.getSecond());

	}

	@Test
	void 유저_로그인_실패_비밀번호_틀림() {
		LoginRequest login = new LoginRequest(
			user.getEmail(),
			"invalid_password"
		);

		assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
	}

	@Test
	@Transactional
	void 유저_로그인_실패_비활성화_상태() {
		user.disabled();
		assertEquals(UserStatus.DISABLED, user.getStatus());
		LoginRequest login = new LoginRequest(
			user.getEmail(),
			rawPassword
		);

		assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
	}

	@Test
	@Transactional
	void 유저_로그인_실패_삭제_상태() {
		user.delete();
		assertEquals(UserStatus.DELETED, user.getStatus());
		LoginRequest login = new LoginRequest(
			user.getEmail(),
			rawPassword
		);

		assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
	}

	@Test
	@Transactional
	void 유저_로그인_실패_탈퇴_상태() {
		user.retired();
		assertEquals(UserStatus.RETIRED, user.getStatus());
		LoginRequest login = new LoginRequest(
			user.getEmail(),
			rawPassword
		);

		assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
	}

	@Test
	void 유저_회원가입_이메일_인증_성공_redis_저장_데이터_존재() {
		SignupRequest.SignupEmail signupEmail = new SignupRequest.SignupEmail(
			email
		);
		authService.sendAuthCode(signupEmail);
		String value = redisCache.get(
			RedisKey.SIGNUP_CODE.key(signupEmail.email()),
			String.class
		);
		assertNotNull(value);
		log.info("authCode :: {}", value);
	}

	@Test
	void 유저_회원가입_이메일_인증_실패_redis_저장_데이터_미존재() {
		String differentEmail = "test@email.com";
		SignupRequest.SignupEmail signupEmail = new SignupRequest.SignupEmail(
			email
		);
		authService.sendAuthCode(signupEmail);
		String value = redisCache.get(
			RedisKey.SIGNUP_CODE.key(differentEmail),
			String.class
		);
		assertNull(value);
		log.info("authCode :: {}", value);
	}

	@Test
	void 유저_회원가입_이메일_검증_redis_저장_데이터_존재() {
		String authCode = "123123";
		redisCache.set(
			RedisKey.SIGNUP_CODE,
			email,
			authCode
		);

		SignupRequest.VerifySignupCode verifyRequest = new SignupRequest.VerifySignupCode(
			email,
			authCode
		);

		authService.verifySignupCode(verifyRequest);

		Boolean isVerify = redisCache.get(
			RedisKey.SIGNUP_VERIFIED.key(email),
			Boolean.class
		);
		assertNotNull(isVerify);
		assertTrue(isVerify);
	}

	@Test
	void 유저_회원가입_이메일_검증_실패_이메일_키값_없음() {
		String differentEmail = "test@email.com";
		String authCode = "123123";
		redisCache.set(
			RedisKey.SIGNUP_CODE,
			email,
			authCode
		);

		SignupRequest.VerifySignupCode verifyRequest =
			new SignupRequest.VerifySignupCode(
				differentEmail, authCode
			);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.verifySignupCode(verifyRequest)
		);
	}

	@Test
	void 유저_회원가입_이메일_검증_실패_인증코드_틀림() {
		String authCode = "123123";
		String differentCode = "999999";
		redisCache.set(
			RedisKey.SIGNUP_CODE,
			email,
			authCode
		);

		SignupRequest.VerifySignupCode verifyRequest =
			new SignupRequest.VerifySignupCode(
				email, differentCode
			);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.verifySignupCode(verifyRequest)
		);
	}

	@Test
	@Transactional
	void 유저_비밀번호_초기화_성공() {
		ResetPasswordRequest resetRequest = new ResetPasswordRequest(
			user.getEmail()
		);

		authService.resetPassword(resetRequest);

		assertEquals(false, passwordEncoder.matches(rawPassword, user.getPassword()));

	}

	@Test
	void 유저_비밀번호_초기화_실패_계정_없음() {
		String notExistsEmail = "test@email.com";
		ResetPasswordRequest resetRequest = new ResetPasswordRequest(
			notExistsEmail
		);
		assertThrowsExactly(
			CustomException.class, () ->
				authService.resetPassword(resetRequest)
		);
	}

	@Test
	void 기사_회원가입_성공() {
		SignupRequest.SignupCourier signup = new SignupRequest.SignupCourier(
			"테스트기사",
			email,
			rawPassword,
			Gender.MALE
		);

		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);

		authService.signupCourier(signup);

		CourierEntity savedCourier = courierRepository.findByEmail(email).orElse(null);
		assertNotNull(savedCourier);

	}

	@Test
	void 기사_회원가입_실패_이메일_검증_실패_이메일_키값_없음() {
		String differentEmail = "test@email.com";

		SignupRequest.SignupCourier signup = new SignupRequest.SignupCourier(
			"테스트기사",
			differentEmail,
			rawPassword,
			Gender.MALE
		);

		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.signupCourier(signup)
		);
	}

	@Test
	void 기사_회원가입_실패_email_중복() {

		SignupRequest.SignupCourier firstSignup =
			new SignupRequest.SignupCourier(
				"테스트기사_1", email, rawPassword, Gender.MALE
			);

		SignupRequest.SignupCourier secondSignup =
			new SignupRequest.SignupCourier(
				"테스트기사_2", email, rawPassword, Gender.MALE
			);

		redisCache.set(
			RedisKey.SIGNUP_VERIFIED,
			email,
			true
		);

		authService.signupCourier(firstSignup);

		assertThrowsExactly(
			CustomException.class, () ->
				authService.signupCourier(secondSignup)
		);
	}

	@Test
	void 비밀번호_초기화_요청_성공() {
		ResetPasswordRequest request = new ResetPasswordRequest(
			user.getEmail()
		);

		authService.requestResetPassword(request);

		PasswordRequestEntity passwordRequest = passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				user, PasswordRequestStatus.PENDING, PasswordRequestType.RESET
			).orElse(null);

		assertNotNull(passwordRequest);
		assertEquals(passwordRequest.getAccount().getId(), user.getId());
		assertEquals(passwordRequest.getStatus(), PasswordRequestStatus.PENDING);

		log.info("request account email : {}", passwordRequest.getAccount().getEmail());
	}

	@Test
	void 비밀번호_초기화_요청_실패_데이터_존재() {
		ResetPasswordRequest request = new ResetPasswordRequest(
			user.getEmail()
		);

		authService.requestResetPassword(request);

		PasswordRequestEntity passwordRequest = passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				user, PasswordRequestStatus.PENDING, PasswordRequestType.RESET
			).orElse(null);

		assertNotNull(passwordRequest);

		assertThatThrownBy(
			() -> authService.requestResetPassword(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("PASSWORD_RESET_ALREADY_REQUESTED");
	}

}
