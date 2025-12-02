package com.kt.api.admin.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 상세 조회 (어드민) - GET /api/admin/users/{userId}")
public class UserDetailTest extends MockMvcTest {

	private final DefaultCurrentUser userDetails = CurrentUserCreator.getAdminUserDetails();
	UserEntity testUser;
	UserEntity testAdmin;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {

		testAdmin = UserEntityCreator.createAdmin();
		testUser = UserEntityCreator.createMember();

		userRepository.save(testUser);
		userRepository.save(testAdmin);
	}

	@Test
	void 회원_상세_조회_성공() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/users/{userId}", testUser.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
		);

		// then
		MvcResult result = actions.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.id").value(testUser.getId().toString()),
				jsonPath("$.data.email").value(testUser.getEmail())
			).andReturn();

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

	@Test
	void 회원_상세_조회_실패__404_NotFound() throws Exception {

		mockMvc.perform(
				get("/api/admin/users/{userId}", UUID.randomUUID())
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
			)
			.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}

}
