package com.kt.api.admin.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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

	DefaultCurrentUser adminDetails;
	@Autowired
	UserRepository userRepository;

	UserEntity testAdmin;
	UserEntity testUser;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
		adminDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 회원_상세_조회_성공__200_OK() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/users/{userId}", testUser.getId())
				.with(user(adminDetails))
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
		// when
		ResultActions actions = mockMvc.perform(
				get("/api/admin/users/{userId}", UUID.randomUUID())
					.with(user(adminDetails))
			);

		// then
		actions.andExpect(status().isNotFound());
	}

	@Test
	void 회원_상세_조회__실패_일반계정에서_시도_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
				get("/api/admin/users/{userId}", testUser.getId())
					.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
			);

		// then
		actions.andExpect(status().isForbidden());
	}
}
