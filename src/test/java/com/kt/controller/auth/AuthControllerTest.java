package com.kt.controller.auth;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.kt.common.MockMvcTest;
import com.kt.config.jwt.JwtTokenProvider;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AuthControllerTest extends MockMvcTest {

	String TEST_EMAIL = "kimdohyun032@gmail.com";

	@Autowired
	private RedisCache redisCache;

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