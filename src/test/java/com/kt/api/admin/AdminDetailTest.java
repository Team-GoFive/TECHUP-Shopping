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
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 상세 조회 (어드민) - GET /api/admin/{adminId}")
public class AdminDetailTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;
	@Autowired
	AdminRepository adminRepository;
	AdminEntity testAdmin;
	AdminEntity testAdmin2;
	UserEntity testUser;
	DefaultCurrentUser adminsDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		testAdmin = AdminCreator.create("테스트어드민1","admin1@test.com");
		testAdmin2 = AdminCreator.create("테스트어드민2","admin2@test.com");
		adminRepository.save(testAdmin);
		adminRepository.save(testAdmin2);
		adminsDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 관리자_상세_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/{adminId}", testAdmin.getId())
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
				jsonPath("$.data.id").value(testAdmin.getId().toString()),
				jsonPath("$.data.email").value(testAdmin.getEmail())
			).andReturn();

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_상세_조회_실패__일반계정에서_시도_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/{adminId}", testAdmin.getId())
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
		);

		// then
		actions.andExpect(status().isForbidden());
	}
}
