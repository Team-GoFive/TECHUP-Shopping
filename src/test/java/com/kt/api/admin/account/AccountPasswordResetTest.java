package com.kt.api.admin.account;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static com.kt.common.CurrentUserCreator.getAdminUserDetails;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("계정 비밀번호 초기화 - PATCH /api/admin/accounts/password-requests/{passwordRequestId}/reset")
public class AccountPasswordResetTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordRequestRepository passwordRequestRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	PasswordRequestEntity passwordRequest;

	UserEntity testUser;
	static final String ORIGIN_PASSWORD = "1231231!";
	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember(
			"bjwnstkdbj@naver.com",
			passwordEncoder.encode(ORIGIN_PASSWORD)
		);
		userRepository.save(testUser);
		setPasswordRequest();
	}

	void setPasswordRequest() {
		passwordRequest = PasswordRequestEntity.create(
			testUser,
			null,
			PasswordRequestType.RESET
		);
		passwordRequestRepository.save(passwordRequest);
	}


	@Test
	void 계정_비밀번호_초기화_성공__200_OK() throws Exception {
		log.info("Before resetPasswordService, originPassword-user.getPassword isMatch: {}",
			passwordEncoder.matches(ORIGIN_PASSWORD, testUser.getPassword())
		);
		ResultActions actions = mockMvc.perform(
			patch(
				"/api/admin/accounts/password-requests/{passwordRequestId}/reset",
				passwordRequest.getId()
			).with(user(getAdminUserDetails()))
		);

		actions.andExpect(status().isOk());

		assertFalse(
			passwordEncoder.matches(ORIGIN_PASSWORD, testUser.getPassword())
		);
		log.info("After resetPasswordService, resetPassword-user.getPassword isMatch: {}",
			passwordEncoder.matches(ORIGIN_PASSWORD, testUser.getPassword())
		);
		assertEquals(PasswordRequestStatus.COMPLETED, passwordRequest.getStatus());

	}
}
