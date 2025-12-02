package com.kt.api.admin.account;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CourierEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.security.DefaultCurrentUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("계정 조회 - DELETE /api/admin/accounts/retire")
public class AccountDetailTest extends MockMvcTest {

	private final DefaultCurrentUser userDetail = CurrentUserCreator.getAdminUserDetails();
	@Autowired
	AccountRepository accountRepository;

	private UserEntity testUser;
	private CourierEntity testCourier;

	@BeforeEach
	public void setUp() {
		testUser = UserEntityCreator.createMember();
		testCourier = CourierEntityCreator.createCourierEntity();
		accountRepository.save(testUser);
		accountRepository.save(testCourier);
	}

	@Test
	void 회원_조회_성공__배송기사_전용_필드_미포함() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(get("/api/admin/accounts/{accountId}", testUser.getId())
			.contentType(MediaType.APPLICATION_JSON).with(user(userDetail)));

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"))
			.andExpect(jsonPath("$.data.workStatus").doesNotExist())
			.andExpect(jsonPath("$.data.birth").exists())
			.andExpect(jsonPath("$.data.mobile").exists());
	}

	@Test
	void 기사_조회_성공__회원_전용_필드_미포함() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/accounts/{accountId}", testCourier.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.with(user(userDetail))
		);

		// then
		actions
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"))
			.andExpect(jsonPath("$.data.birth").doesNotExist())
			.andExpect(jsonPath("$.data.mobile").doesNotExist())
			.andExpect(jsonPath("$.data.workStatus").exists());
	}
}
