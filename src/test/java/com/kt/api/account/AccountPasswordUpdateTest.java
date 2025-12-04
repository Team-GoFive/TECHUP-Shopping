package com.kt.api.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

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
	static final String TEST_PASSWORD = "1234567891011";

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntity.create(
			"회원1",
			"member@test.com",
			passwordEncoder.encode(TEST_PASSWORD),
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);;
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
			TEST_PASSWORD,
			"123456789101112"
		);
		String json = objectMapper.writeValueAsString(accountRequest);

		// when
		ResultActions actions = mockMvc.perform(patch("/api/accounts/{accountId}/password",testUser.getId())
			.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
			.contentType(MediaType.APPLICATION_JSON)
			.content(json)
		);

		// then
		actions.andExpect(status().isOk());
		boolean result = passwordEncoder.matches("123456789101112", savedAccount.getPassword());
		Assertions.assertTrue(result);
	}
}
