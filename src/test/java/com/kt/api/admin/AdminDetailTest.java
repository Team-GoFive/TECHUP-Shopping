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
@DisplayName("관리자 상세 조회 (어드민) - GET /api/admins/{adminId}")
public class AdminDetailTest extends MockMvcTest {
	private final DefaultCurrentUser userDetails = CurrentUserCreator.getAdminUserDetails();
	@Autowired
	UserRepository userRepository;
	UserEntity testAdmin;

	@BeforeEach
	void setUp() {
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
	}

	@Test
	void 관리자_상세_조회_성공() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(get(
				"/api/admins/{adminId}", testAdmin.getId()
			).with(user(userDetails))
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

}
