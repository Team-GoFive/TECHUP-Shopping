package com.kt.api.admin.account;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;

import com.kt.constant.PasswordRequestType;
import com.kt.constant.UserRole;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.CurrentUserCreator.getAdminUserDetails;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@DisplayName("계정 비밀번호 초기화 - GET /api/admin/accounts/password-requests")
public class AccountPasswordRequestSearchTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	PasswordRequestRepository passwordRequestRepository;

	UserEntity testUser1;
	UserEntity testUser2;
	static final String ORIGIN_PASSWORD = "1231231!";
	@BeforeEach
	void setUp() {
		testUser1 = UserEntityCreator.createMember(
			"test1@naver.com",
			passwordEncoder.encode(ORIGIN_PASSWORD)
		);
		testUser2 = UserEntityCreator.createMember(
			"test2@naver.com",
			passwordEncoder.encode(ORIGIN_PASSWORD)
		);
		userRepository.save(testUser1);
		userRepository.save(testUser2);
	}

	void setPasswordRequest() {
		PasswordRequestEntity firstPasswordRequest = PasswordRequestEntity.create(
			testUser1,
			null,
			PasswordRequestType.RESET
		);

		PasswordRequestEntity secondPasswordRequest = PasswordRequestEntity.create(
			testUser1,
			null,
			PasswordRequestType.RESET
		);
		passwordRequestRepository.save(firstPasswordRequest);
		passwordRequestRepository.save(secondPasswordRequest);
	}

	@Test
	void 비밀번호_변경_및_초기화_요청_검색__200_OK() throws Exception {

		ResultActions actions = mockMvc.perform(
			get("/api/admin/accounts/password-requests")
				.param("page", "1")
				.param("size", "10")
				.param("role", UserRole.MEMBER.name())
				.param("status", "")
				.param("requestType", "")
				.param("searchKeyword", "")
				.with(user(getAdminUserDetails()))
		);

		actions.andExpect(status().isOk());

	}
}
