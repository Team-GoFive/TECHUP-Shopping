package com.kt.api.admin.user;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 활성화 (어드민)  - PATCH /api/admin/users{userId}/enabled")
public class UserEnabledTest extends MockMvcTest {

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
	void 회원_활성화_성공_200() throws Exception {
		// given
		testUser.disabled();

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/admin/users/{userId}/enabled", testUser.getId())
				.with(user(adminDetails))
		);

		// then
		MvcResult result = actions.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		UserEntity savedUser = userRepository.findByIdOrThrow(testUser.getId());
		assertEquals(UserStatus.ENABLED, savedUser.getStatus());

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 회원_활성화_실패_404_NotFound() throws Exception {

		mockMvc.perform(
				patch("/api/admin/users/{userId}/enabled", UUID.randomUUID())
					.with(user(adminDetails))
			)
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void 회원_활성화_실패__일반유저_403() throws Exception {
		// given
		testUser.disabled();
		DefaultCurrentUser memberDetails = CurrentUserCreator.getMemberUserDetails(testUser.getId());

		// when
		mockMvc.perform(
			patch("/api/admin/users/{userId}/enabled", testUser.getId())
				.with(user(memberDetails))
		).andExpect(status().isForbidden());
	}

}
