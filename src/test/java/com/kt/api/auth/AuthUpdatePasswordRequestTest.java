package com.kt.api.auth;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.entity.PasswordRequestEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.PasswordRequestRepository;
import com.kt.repository.user.UserRepository;

import com.kt.security.CurrentUser;

import com.kt.util.EncryptUtil;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.kt.common.CurrentUserCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Slf4j
@DisplayName("비밀번호 변경 요청 - POST /api/auth/password-update/requests")
public class AuthUpdatePasswordRequestTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordRequestRepository passwordRequestRepository;

	UserEntity testUser;
	CurrentUser currentUser;
	static final String TEST_KEY = "techup-shopping-encrypt-test-key";
	@BeforeEach
	void setUp() {
		saveTestUser();
		currentUser = getAdminUserDetails();
		EncryptUtil.loadKey(TEST_KEY);
	}

	void saveTestUser() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
	}

	@Test
	void 계정_비밀번호_변경_요청_성공__200_OK() throws Exception {
		String updatePassword = "123123!!";
		PasswordManagementRequest.PasswordUpdate request =
			new PasswordManagementRequest.PasswordUpdate(
				testUser.getEmail(), updatePassword
			);

		ResultActions actions = mockMvc.perform(
			post("/api/auth/password-update/requests")
				.with(user(getAdminUserDetails()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		actions.andDo(print());

		PasswordRequestEntity passwordRequest =
			passwordRequestRepository.findByAccountAndStatusAndRequestType(
				testUser, PasswordRequestStatus.PENDING, PasswordRequestType.UPDATE
			).orElse(null);

		assertNotNull(passwordRequest);

		assertEquals(testUser.getId(), passwordRequest.getAccount().getId());

		String decryptPassword = EncryptUtil.decrypt(
			passwordRequest.getEncryptedPassword()
		);
		assertEquals(updatePassword, decryptPassword);

		log.info("decrypt password :: {}", decryptPassword);
	}
}
