package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.infra.redis.RedisCache;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("이메일 인증 코드 전송 - POST /api/auth/email/code")
public class AuthSendAuthCodeTest extends MockMvcTest {

	@Autowired
	RedisCache redisCache;

	UserEntity testUser;
	static final String EMAIL = "bjwnstkdbj@naver.com";

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
