package com.kt.api.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kt.common.UserEntityCreator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@DisplayName("계정 비밀번호 변경 - PATCH /api/accounts/{accountId}/password")
public class AccountPasswordUpdateTest extends MockMvcTest {
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	UserEntity testUser;
	DefaultCurrentUser userDetails;
	static final String TEST_EMAIL = "user@test.com";
	static final String ORIGIN_PASSWORD = "1231231!";
	static final String UPDATE_PASSWORD = "1231231!@";

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntityCreator.create(
			TEST_EMAIL, passwordEncoder.encode(ORIGIN_PASSWORD)
		);
		userRepository.save(testUser);

		userDetails = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			testUser.getRole()
		);
	}

	@Test
	void 비밀번호변경_성공__200_OK() throws Exception {
		// given
		AbstractAccountEntity savedAccount = accountRepository.findByIdOrThrow(testUser.getId());
		AccountRequest.UpdatePassword accountRequest = new AccountRequest.UpdatePassword(
			ORIGIN_PASSWORD,
			UPDATE_PASSWORD
		);
		String json = objectMapper.writeValueAsString(accountRequest);

		// when
		ResultActions actions = mockMvc.perform(
			patch("/api/accounts/{accountId}/password",testUser.getId())
			.with(user(userDetails))
			.contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);

		// then
		actions.andExpect(status().isOk());
		boolean result = passwordEncoder.matches(UPDATE_PASSWORD, savedAccount.getPassword());
		Assertions.assertTrue(result);
	}
}
