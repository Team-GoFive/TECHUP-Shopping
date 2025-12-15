package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.infra.redis.RedisCache;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
@DisplayName("이메일 인증 코드 검증 - POST /api/auth/email/verify")
public class AuthVerifyCodeTest extends MockMvcTest {

	@Autowired
	RedisCache redisCache;

	static final String AUTH_CODE = "123123";
	static final String EMAIL = "bjwnstkdbj@naver.com";

	@BeforeEach
	void init() {
		redisCache.set(RedisKey.SIGNUP_CODE, EMAIL, AUTH_CODE);
	}

	@Test
	void 인증번호_검증_성공_200__OK() throws Exception {
		SignupRequest.VerifySignupCode request = new SignupRequest.VerifySignupCode(
			EMAIL, AUTH_CODE
		);

		ResultActions actions = mockMvc.perform(
			post("/api/auth/email/verify")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());
		Boolean isVerify = redisCache.get(
			RedisKey.SIGNUP_VERIFIED.key(EMAIL),
			Boolean.class
		);

		assertEquals(Boolean.TRUE, isVerify);
		log.info("isVerify :: {}", isVerify);

	}
}
