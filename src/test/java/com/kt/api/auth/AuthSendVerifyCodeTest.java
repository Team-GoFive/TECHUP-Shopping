package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;


import static com.kt.common.UserEntityCreator.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("비밀번호 초기화 요청 - POST /api/auth/email/code")
public class AuthSendVerifyCodeTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	RedisCache redisCache;
	@Autowired
	PasswordEncoder passwordEncoder;

	UserEntity testUser;
	static final String EMAIL = "bjwnstkdbj@naver.com";
	static final String PASSWORD = "1231231!";

	@BeforeEach
	void setUp() {
		saveTestUser();
	}

	void saveTestUser() {
		testUser = createMember(
			EMAIL, passwordEncoder.encode(PASSWORD)
		);
		userRepository.save(testUser);
	}

	@Test
	void 인증번호_발송_성공__200__OK() throws Exception {
		SignupRequest.SignupEmail request = new SignupRequest.SignupEmail(
			EMAIL
		);
		ResultActions actions = mockMvc.perform(
			post("/api/auth/email/code")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());

		String authCode = redisCache.get(
			RedisKey.SIGNUP_CODE.key(EMAIL),
			String.class
		);

		log.info("authCode :: {}", authCode);
		assertNotNull(authCode);
	}

}
