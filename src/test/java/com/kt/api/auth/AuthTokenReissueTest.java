package com.kt.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.kt.common.MockMvcTest;

import com.kt.common.UserEntityCreator;
import com.kt.config.jwt.JwtTokenProvider;
import com.kt.constant.TokenType;
import com.kt.constant.redis.RedisKey;
import com.kt.domain.dto.request.TokenReissueRequest;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
@DisplayName("계정로그인 - POST /api/auth/token/reissue")
public class AuthTokenReissueTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	RedisCache redisCache;

	UserEntity testUser;
	String accessToken;
	String refreshToken;

	static final String EMAIL = "bjwnstkdbj@naver.com";
	static final String PASSWORD = "123123!@";

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create(
			EMAIL, passwordEncoder.encode(PASSWORD)
		);
		userRepository.save(testUser);

		accessToken = jwtTokenProvider.create(
			testUser.getId(),
			testUser.getEmail(),
			testUser.getRole(),
			TokenType.ACCESS
		);

		refreshToken = jwtTokenProvider.create(
			testUser.getId(),
			testUser.getEmail(),
			testUser.getRole(),
			TokenType.REFRESH
		);
		String refreshJti = jwtTokenProvider.getJti(refreshToken);
		redisCache.set(
			RedisKey.REFRESH_TOKEN,
			testUser.getId(),
			refreshJti
		);
	}

	@Test
	void 토큰_재발급_성공() throws Exception {
		TokenReissueRequest request = new TokenReissueRequest(refreshToken);

		MvcResult result = mockMvc.perform(
				post("/api/auth/token/reissue")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andReturn();
		String response = result.getResponse().getContentAsString();
		JsonNode json = objectMapper.readTree(response);

		String reissuedAccessToken = json.get("data").get("accessToken").asText();
		String reissuedRefreshToken = json.get("data").get("refreshToken").asText();
		assertNotNull(reissuedAccessToken);
		assertNotNull(reissuedRefreshToken);
		assertNotEquals(accessToken, reissuedAccessToken);
		assertNotEquals(refreshToken, reissuedRefreshToken);
	}


}
