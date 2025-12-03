package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.user.UserRepository;

import com.kt.security.CurrentUser;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.kt.common.UserEntityCreator.createMember;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static com.kt.common.CurrentUserCreator.getAdminUserDetails;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DisplayName("비밀번호 초기화 요청 - POST /api/auth/password/reset-requests")
public class AuthResetPasswordRequestTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordRequestRepository passwordRequestRepository;

	UserEntity testUser;
	CurrentUser currentUser;
	@BeforeEach
	void setUp() {
		saveTestUser();
		currentUser = getAdminUserDetails();
	}

	void saveTestUser() {
		testUser = createMember();
		currentUser = getAdminUserDetails();
		userRepository.save(testUser);
	}

	@Test
	void 계정_비밀번호_초기화_요청_성공__200_OK() throws Exception {
		PasswordManagementRequest.PasswordReset request =
			new PasswordManagementRequest.PasswordReset(
				testUser.getEmail()
			);

		ResultActions actions = mockMvc.perform(
			post("/api/auth/password/reset-requests")
				.with(user(getAdminUserDetails()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());

		PasswordRequestEntity passwordRequest =
			passwordRequestRepository.findByAccountAndStatusAndRequestType(
				testUser, PasswordRequestStatus.PENDING, PasswordRequestType.RESET
			).orElse(null);

		assertNotNull(passwordRequest);
		assertEquals(
			testUser.getId(),
			passwordRequest.getAccount().getId()
		);
		log.info("passRequest account id : {}", passwordRequest.getAccount().getId());

	}
}
