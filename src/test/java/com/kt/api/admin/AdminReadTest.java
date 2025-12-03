package com.kt.api.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

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
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 목록 조회 (어드민) - GET /api/admins")
public class AdminReadTest extends MockMvcTest {
	DefaultCurrentUser adminsDetails;
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
		adminsDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 관리자_목록_조회_성공() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/api/admin")
				.param("page", "1")
				.param("size", "10")
				.param("role", UserRole.ADMIN.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "")
				.with(user(adminsDetails))
		);

		MvcResult result = actions
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.totalCount").value(1),
				jsonPath("$.data.totalPages").value(1)
			).andReturn();

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);

	}

	@Test
	void 관리자_목록_조회_실패__어드민_아님_403() throws Exception {
		DefaultCurrentUser memberDetails = CurrentUserCreator.getMemberUserDetails(testUser.getId());

		mockMvc.perform(
			get("/api/admin")
				.param("page", "1")
				.param("size", "10")
				.param("role", UserRole.ADMIN.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "")
				.with(user(memberDetails))
		).andExpectAll(
			status().isForbidden());
	}

}
