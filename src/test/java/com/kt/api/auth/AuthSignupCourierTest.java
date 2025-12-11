package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.SignupRequest;

import com.kt.domain.entity.CourierEntity;
import com.kt.infra.redis.RedisCache;
import com.kt.repository.courier.CourierRepository;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
@DisplayName("이메일 인증 코드 검증 - POST /api/auth/signup/courier")
public class AuthSignupCourierTest extends MockMvcTest {

	@Autowired
	RedisCache redisCache;

	@Autowired
	CourierRepository courierRepository;

	static final String EMAIL = "bjwnstkdbj@naver.com";

	@BeforeEach
	void init() {
		redisCache.set(RedisKey.SIGNUP_VERIFIED, EMAIL, true);
	}
	@Test
	void 유저_회원갸입_성공_200__OK() throws Exception {

		SignupRequest.SignupCourier request = new SignupRequest.SignupCourier(
			"테스트기사",
			EMAIL,
			"123123",
			Gender.MALE
		);

		ResultActions actions = mockMvc.perform(
			post("/api/auth/signup/courier")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());

		CourierEntity courier = courierRepository.findByEmailOrThrow(EMAIL);
		assertNotNull(courier);
		assertNotNull(courier.getId());
		log.info("courier id : {}", courier.getId());
		assertEquals(courier.getEmail(), EMAIL);

	}
}
