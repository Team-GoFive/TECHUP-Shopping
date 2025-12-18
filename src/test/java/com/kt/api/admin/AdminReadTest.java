package com.kt.api.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.kt.common.AdminCreator;
import com.kt.domain.entity.AdminEntity;

import com.kt.repository.admin.AdminRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 목록 조회 (어드민) - GET /api/admins")
public class AdminReadTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AdminRepository adminRepository;

	AdminEntity testAdmin;
	UserEntity testUser;
	DefaultCurrentUser adminsDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		testAdmin = AdminCreator.create();
		userRepository.save(testUser);
		adminRepository.save(testAdmin);
		adminsDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 관리자_목록_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin")
				.param("page", "1")
				.param("size", "10")
				.param("role", AccountRole.ADMIN.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "")
				.with(user(adminsDetails))
		);

		// then
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
	void 관리자_목록_조회_실패__일반유저계정_시도_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin")
				.param("page", "1")
				.param("size", "10")
				.param("role", AccountRole.ADMIN.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "")
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
		);

		// then
		actions.andExpect(status().isForbidden());
	}
}
