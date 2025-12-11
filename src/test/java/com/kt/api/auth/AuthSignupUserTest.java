package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("이메일 인증 코드 검증 - POST /api/auth/signup/user")
public class AuthSignupUserTest extends MockMvcTest {
	@Autowired
	RedisCache redisCache;

	@Autowired
	UserRepository userRepository;

	static final String EMAIL = "bjwnstkdbj@naver.com";

	@BeforeEach
	void init() {
		redisCache.set(RedisKey.SIGNUP_VERIFIED, EMAIL, true);
	}

	@Test
	void 유저_회원갸입_성공_200__OK() throws Exception {

		SignupRequest.SignupUser request = new SignupRequest.SignupUser(
			"테스트황",
			EMAIL,
			"1231231!",
			Gender.MALE,
			LocalDate.of(1998, 3, 13),
			"010-1234-1234"
		);

		ResultActions actions = mockMvc.perform(
			post("/api/auth/signup/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());

		UserEntity user = userRepository.findByEmailOrThrow(EMAIL);
		assertNotNull(user);
		assertNotNull(user.getId());
		assertEquals(user.getEmail(), EMAIL);
		log.info("user.getId ::  {}", user.getId());
	}

}
