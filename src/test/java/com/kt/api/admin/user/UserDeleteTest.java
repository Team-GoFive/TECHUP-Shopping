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
@DisplayName("유저 삭제 (어드민) - GET /api/admin/users/{userId}/disabled")
public class UserDeleteTest extends MockMvcTest {
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
	void 회원_삭제_성공() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/admin/users/{userId}/removed", testUser.getId())
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
		assertEquals(UserStatus.DELETED, savedUser.getStatus());

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 회원_삭제_실패__404_NotFound() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/admin/users/{userId}/removed", UUID.randomUUID())
				.with(user(adminDetails))
		);

		// then
		actions.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}

	@Test
	void 회원_삭제_실패__일반계정_403() throws Exception {
		DefaultCurrentUser memberDetails = CurrentUserCreator.getMemberUserDetails(testUser.getId());
		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/admin/users/{userId}/removed", testUser.getId())
				.with(user(memberDetails))
		);

		// then
		actions.andDo(print())
			.andExpectAll(
				status().isForbidden());
	}
}
