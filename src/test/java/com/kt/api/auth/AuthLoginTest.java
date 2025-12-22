package com.kt.api.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.entity.UserEntity;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("계정로그인 - POST /api/auth/login")
public class AuthLoginTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	static final String EMAIL = "bjwnstkdbj@naver.com";
	static final String PASSWORD = "123123!@";
	@BeforeEach
	void init() {
		saveUser();
	}

	void saveUser() {
		UserEntity user = UserEntityCreator.create(
			EMAIL, passwordEncoder.encode(PASSWORD)
		);
		userRepository.save(user);
		assertNotNull(user);
	}

	@Test
	void 로그인_성공_200__OK() throws Exception {
		LoginRequest request = new LoginRequest(
			EMAIL, PASSWORD
		);
		MvcResult result = mockMvc.perform(
			post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		String response = result.getResponse().getContentAsString();
		JsonNode json = objectMapper.readTree(response);
		String accessToken = json.get("data").get("accessToken").asText();
		String refreshToken = json.get("data").get("refreshToken").asText();

		log.info("accessToken :: {}", accessToken);
		log.info("refreshToken :: {}", refreshToken);
	}
}
