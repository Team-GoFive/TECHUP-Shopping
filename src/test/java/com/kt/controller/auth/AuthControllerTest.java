package com.kt.controller.auth;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.config.jwt.JwtTokenProvider;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.ResetPasswordRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

	String TEST_EMAIL = "kimdohyun032@gmail.com";

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private RedisCache redisCache;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		accountRepository.deleteAll();
		redisCache.delete(RedisKey.SIGNUP_CODE.key(TEST_EMAIL));
	}

	@Test
	void 비밀번호_초기화_성공() throws Exception {
		String email = "dd@com";
		String password = "123456";
		// given

		UserEntity user = UserEntity.create(
			"김도현",
			email,
			passwordEncoder.encode(password),
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(2000, 1, 1),
			"010-3333-2222"
		);
		UserEntity savedUser = userRepository.save(user);
		ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(savedUser.getEmail());

		// when
		mockMvc.perform(patch("/api/auth/init-password")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(resetPasswordRequest)))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			);

		// then
		UserEntity updatedUser = userRepository.findByIdOrThrow(savedUser.getId());
		assertThat(passwordEncoder.matches(password, updatedUser.getPassword())).isFalse();
	}

	@Test
	void 인증코드_발송_성공() throws Exception {

		// given
		var request = new SignupRequest.SignupEmail(TEST_EMAIL);

		// when
		mockMvc.perform(
				post("/api/auth/email/code")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			);

		// then
		assertThat(redisCache.hasKey(RedisKey.SIGNUP_CODE.key(TEST_EMAIL))).isEqualTo(true);
		String redisAuthCode = redisCache.get(
			RedisKey.SIGNUP_CODE.key(TEST_EMAIL),
			String.class
		);
		log.info("이메일 인증코드: - 발송: {}", redisAuthCode);
		assertThat(redisAuthCode).isNotNull();
	}

	@Test
	void 인증코드_인증_성공() throws Exception {

		// given
		String redisAuthCode = "123456";
		redisCache.set(RedisKey.SIGNUP_CODE, TEST_EMAIL, redisAuthCode);

		var request = new SignupRequest.VerifySignupCode(TEST_EMAIL, redisAuthCode);

		// then
		assertThat(redisAuthCode).isNotNull();
		log.info("이메일 인증코드 - 인증: {}", redisAuthCode);

		mockMvc.perform(post("/api/auth/email/verify")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			);
	}

	@Test
	void 멤버_회원가입_성공() throws Exception {
		redisCache.set(RedisKey.SIGNUP_VERIFIED, TEST_EMAIL, true);
		// then
		SignupRequest.SignupMember verifiedEmailUser = new SignupRequest.SignupMember(
			"테스트",
			TEST_EMAIL,
			"비밀번호",
			Gender.MALE,
			LocalDate.now(),
			"010-1020-1200"
		);

		// then
		mockMvc.perform(post("/api/auth/signup/member")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifiedEmailUser)))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			);
	}

	@Test
	void 배송기사_회원가입_성공() throws Exception {
		redisCache.set(RedisKey.SIGNUP_VERIFIED, TEST_EMAIL, true);
		// then
		SignupRequest.SignupCourier verifiedEmailCourier = new SignupRequest.SignupCourier(
			"테스트",
			TEST_EMAIL,
			"비밀번호",
			Gender.MALE
		);

		// then
		mockMvc.perform(post("/api/auth/signup/courier")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(verifiedEmailCourier)))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			);
	}

	@Test
	void 로그인_성공() throws Exception {

		String email = "dd@com";
		String password = "123456";
		// given

		UserEntity user = UserEntity.create(
			"김도현",
			email,
			passwordEncoder.encode(password),
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(2000, 1, 1),
			"010-3333-2222"
		);
		userRepository.save(user);

		LoginRequest loginInfo = new LoginRequest(email, password);

		// when
		MvcResult result = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginInfo)))
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		// then
		String responseBody = result.getResponse().getContentAsString();
		JsonNode json = objectMapper.readTree(responseBody);

		String accessToken = json.get("data").get("accessToken").asText();
		String refreshToken = json.get("data").get("refreshToken").asText();

		assertThatCode(() -> jwtTokenProvider.validateToken(accessToken)).doesNotThrowAnyException();
		assertThatCode(() -> jwtTokenProvider.validateToken(refreshToken)).doesNotThrowAnyException();
	}

}