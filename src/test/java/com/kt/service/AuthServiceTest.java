package com.kt.service;

import static com.kt.common.SignupCourierRequestCreator.*;
import static com.kt.common.SignupMemberRequestCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.util.EncryptUtil;
import com.mysema.commons.lang.Pair;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AuthServiceTest {

	@Autowired
	AuthServiceImpl authService;
	@Autowired
	UserRepository userRepository;
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

	UserEntity testMember;

	String testEmail = "test@test.com";
	String rawPassword = "1234";

	@BeforeEach
	void setUp() {
		testMember = createMember(testEmail, passwordEncoder.encode(rawPassword));
		userRepository.save(testMember);
		String testKey = "techup-shopping-encrypt-test-key";
		EncryptUtil.loadKey(testKey);
	}

	@AfterEach
	void clearUp() {
		var connection = redisTemplate.getConnectionFactory().getConnection();
		connection.flushAll();
	}

	@Test
	void 맴버_회원가입_성공() {
		// given
		String email = "member@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED, email, true);

		// when
		SignupRequest.SignupMember request = createSignupMemberRequest(email);
		authService.signupMember(request);

		// then
		UserEntity member = userRepository.findByEmailOrThrow(request.email());
		assertEquals(email, member.getEmail());
	}
	// 해야함 인증 정보 null 이거나 false 일때 에러 에러

	@Test
	void 맴버_회원가입_실패_인증정보_없음_시간초과() throws InterruptedException {
		// given
		String email = "member@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED.key(email), true, Duration.ofMillis(100));
		Thread.sleep(200);

		// when
		SignupRequest.SignupMember request = createSignupMemberRequest(email);

		// then
		assertThrowsExactly(
			CustomException.class,
			() -> authService.signupMember(request),
			ErrorCode.AUTH_EMAIL_UNVERIFIED.getMessage()
		);
	}

	@Test
	void 맴버_회원가입_실패_인증정보_없음_이메일_키값() {
		// when and then
		SignupRequest.SignupMember request = createSignupMemberRequest();

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.signupMember(request)
		);
		assertEquals(ErrorCode.AUTH_EMAIL_UNVERIFIED, exception.error());
	}

	@Test
	void 맴버_회원가입_실패_email_중복() {
		// given
		String email = "member@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED, email, true);
		SignupRequest.SignupMember firstSignup = createSignupMemberRequest(email);
		authService.signupMember(firstSignup);

		// when and then
		SignupRequest.SignupMember secondSignup = createSignupMemberRequest(email);

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.signupMember(secondSignup)
		);
		assertEquals(ErrorCode.DUPLICATED_EMAIL, exception.error());
	}

	@Test
	void 유저_로그인_성공() {
		// when
		LoginRequest login = new LoginRequest(testEmail, rawPassword);
		Pair<String, String> result = authService.login(login);

		// then
		assertNotNull(result.getFirst());
		assertNotNull(result.getSecond());
		log.info("access token : {}", result.getFirst());
		log.info("refresh token : {}", result.getSecond());
	}

	@Test
	void 유저_로그인_실패_비밀번호_틀림() {
		// when and then
		LoginRequest login = new LoginRequest(testEmail, "invalid_password");

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
		assertEquals(ErrorCode.AUTH_FAILED_LOGIN, exception.error());
	}

	@Test
	@Transactional
	void 유저_로그인_실패_비활성화_상태() {
		// given
		testMember.disabled();

		// when and then
		LoginRequest login = new LoginRequest(testEmail, rawPassword);

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
		assertEquals(ErrorCode.AUTH_ACCOUNT_DISABLED, exception.error());
	}

	@Test
	@Transactional
	void 유저_로그인_실패_삭제_상태() {
		// given
		testMember.delete();

		// when and then
		LoginRequest login = new LoginRequest(testEmail, rawPassword);

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
		assertEquals(ErrorCode.AUTH_ACCOUNT_DELETED, exception.error());
	}

	@Test
	@Transactional
	void 유저_로그인_실패_탈퇴_상태() {
		// given
		testMember.retired();

		// when and then
		LoginRequest login = new LoginRequest(testEmail, rawPassword);

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.login(login)
		);
		assertEquals(ErrorCode.AUTH_ACCOUNT_RETIRED, exception.error());
	}

	@Test
	void 이메일_인증_코드_발송_후_해당_이메일에_대한_redis_키_값_저장_성공() {
		// when
		SignupRequest.SignupEmail request = new SignupRequest.SignupEmail(testEmail);
		authService.sendAuthCode(request);

		// then
		String value = redisCache.get(RedisKey.SIGNUP_CODE.key(request.email()), String.class);
		assertNotNull(value);
		log.info("authCode : {}", value);
	}

	@Test
	void 인증_코드를_보내지_않은_이메일은_redis_키_값으로_저장_되지_않음() {
		String noneRequestEmail = "test@email.com";

		String value = redisCache.get(RedisKey.SIGNUP_CODE.key(noneRequestEmail), String.class);
		assertNull(value);
		log.info("authCode :: {}", value);
	}

	@Test
	void 인증코드로_이메일_인증_성공() {
		// given
		String authCode = "123123";
		redisCache.set(RedisKey.SIGNUP_CODE, testEmail, authCode);

		// when
		SignupRequest.VerifySignupCode request = new SignupRequest.VerifySignupCode(testEmail, authCode);
		authService.verifySignupCode(request);

		Boolean isVerify = redisCache.get(RedisKey.SIGNUP_VERIFIED.key(testEmail), Boolean.class);
		assertNotNull(isVerify);
		assertTrue(isVerify);
	}

	@Test
	void 이메일_인증_시_이메일이_일치하지_않으면_인증_실패() {
		// given
		String differentEmail = "test@email.com";
		String authCode = "123123";
		redisCache.set(RedisKey.SIGNUP_CODE, testEmail, authCode);

		// when and then
		CustomException exception = assertThrowsExactly(
			CustomException.class, () ->
				authService.verifySignupCode(new SignupRequest.VerifySignupCode(differentEmail, authCode))
		);
		assertEquals(ErrorCode.AUTH_CODE_UNAVAILABLE, exception.error());
	}

	@Test
	void 이메일_인증_시_인증코드가_일치하지_않으면_인증_실패() {
		// given
		redisCache.set(RedisKey.SIGNUP_CODE, testEmail, "123123");

		// when and then
		CustomException exception = assertThrowsExactly(
			CustomException.class, () ->
				authService.verifySignupCode(new SignupRequest.VerifySignupCode(testEmail, "999999"))
		);
		assertEquals(ErrorCode.AUTH_CODE_INVALID, exception.error());
	}

	@Test
	@Transactional
	void 계정_비밀번호_초기화_성공() {

		// when
		PasswordManagementRequest.PasswordInit request
			= new PasswordManagementRequest.PasswordInit(testMember.getEmail());

		authService.initPassword(request);

		// then
		assertEquals(false, passwordEncoder.matches(rawPassword, testMember.getPassword()));
	}

	@Test
	void 계정_비밀번호_초기화_실패_계정_없음() {
		// when and then
		PasswordManagementRequest.PasswordInit resetRequest =
			new PasswordManagementRequest.PasswordInit("none-exist@email.com");

		CustomException exception = assertThrowsExactly(
			CustomException.class, () ->
				authService.initPassword(resetRequest)
		);
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.error());
	}

	@Test
	void 기사_회원가입_성공() {
		// given
		String email = "courier@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED, email, true);

		// when
		SignupRequest.SignupCourier request = createSignupCourierRequest(email);
		authService.signupCourier(request);

		// then
		CourierEntity courier = courierRepository.findByEmailOrThrow(request.email());
		assertEquals(email, courier.getEmail());
	}

	@Test
	void 기사_회원가입_실패_이메일_검증_실패_이메일_키값_없음() {
		// when and then
		SignupRequest.SignupCourier request = createSignupCourierRequest();

		CustomException exception = assertThrowsExactly(
			CustomException.class,
			() -> authService.signupCourier(request)
		);
		assertEquals(ErrorCode.AUTH_EMAIL_UNVERIFIED, exception.error());
	}

	@Test
	void 기사_회원가입_실패_email_중복() {
		// given
		String email = "courier@email.com";
		redisCache.set(RedisKey.SIGNUP_VERIFIED, email, true);
		SignupRequest.SignupCourier firstSignup = createSignupCourierRequest(email);
		authService.signupCourier(firstSignup);

		// when and then
		SignupRequest.SignupCourier secondSignup = createSignupCourierRequest(email);

		CustomException exception = assertThrowsExactly(
			CustomException.class, () ->
				authService.signupCourier(secondSignup)
		);
		assertEquals(ErrorCode.DUPLICATED_EMAIL, exception.error());
	}

	@Test
	void 비밀번호_초기화_요청_성공() {
		// when
		PasswordManagementRequest.PasswordInit request =
			new PasswordManagementRequest.PasswordInit(testMember.getEmail());

		authService.requestPasswordInit(request);

		// then
		PasswordRequestEntity passwordRequest = passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				testMember, PasswordRequestStatus.PENDING, PasswordRequestType.RESET
			).orElse(null);

		assertNotNull(passwordRequest);
		assertEquals(passwordRequest.getAccount().getId(), testMember.getId());
		assertEquals(passwordRequest.getStatus(), PasswordRequestStatus.PENDING);
	}

	@Test
	void 비밀번호_초기화_요청_실패_데이터_존재() {
		// given
		PasswordManagementRequest.PasswordInit request =
			new PasswordManagementRequest.PasswordInit(testMember.getEmail());

		authService.requestPasswordInit(request);

		// when and then
		assertThatThrownBy(() -> authService.requestPasswordInit(request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.PASSWORD_INIT_ALREADY_REQUESTED.name());
	}

	@Test
	void 비밀번호_변경_요청_성공() {
		// when
		String updatePassword = "123123@";
		PasswordManagementRequest.PasswordUpdate request =
			new PasswordManagementRequest.PasswordUpdate(testMember.getEmail(), updatePassword);

		authService.requestPasswordUpdate(request);

		// then
		PasswordRequestEntity passwordRequest = passwordRequestRepository
			.findByAccountAndStatusAndRequestType(
				testMember, PasswordRequestStatus.PENDING, PasswordRequestType.UPDATE
			).orElse(null);

		assertNotNull(passwordRequest);
		assertEquals(passwordRequest.getAccount().getId(), testMember.getId());
		assertEquals(passwordRequest.getStatus(), PasswordRequestStatus.PENDING);
	}

	@Test
	void 비밀번호_변경_요청이_존재하는데_추가_비밀번호_변경_요청_시_기존_데이터_삭제() {
		// given
		String firstUpdatePassword = "111111@";
		PasswordManagementRequest.PasswordUpdate firstRequest =
			new PasswordManagementRequest.PasswordUpdate(testMember.getEmail(), firstUpdatePassword);

		authService.requestPasswordUpdate(firstRequest);

		UUID firstId = Objects.requireNonNull(
				passwordRequestRepository.findByAccountAndStatusAndRequestType(
						testMember,
						PasswordRequestStatus.PENDING,
						PasswordRequestType.UPDATE
					)
					.orElse(null))
			.getId();

		// when
		String secondUpdatePassword = "222222@!";
		PasswordManagementRequest.PasswordUpdate secondRequest =
			new PasswordManagementRequest.PasswordUpdate(testMember.getEmail(), secondUpdatePassword);

		authService.requestPasswordUpdate(secondRequest);

		// then
		UUID secondId = Objects.requireNonNull(
				passwordRequestRepository.findByAccountAndStatusAndRequestType(
						testMember,
						PasswordRequestStatus.PENDING,
						PasswordRequestType.UPDATE
					)
					.orElse(null))
			.getId();

		assertNotEquals(firstId, secondId);
	}

}
