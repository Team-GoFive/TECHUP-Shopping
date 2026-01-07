package com.kt.api.admin.account;

import com.kt.common.CourierEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@Slf4j
@DisplayName("유저 목록 조회 (어드민) - GET /api/admin/accounts")
class AccountReadTest extends MockMvcTest {

	UserEntity testUser;
	CourierEntity testCourier;
	CourierEntity secondTestCourier;
	CourierEntity thirdTestCourier;

	@Autowired
	UserRepository userRepository;
	@Autowired
	CourierRepository courierRepository;

	DefaultCurrentUser userDetails = CurrentUserCreator.getAdminUserDetails();

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		testCourier = CourierEntityCreator.createCourierEntity();
		secondTestCourier = CourierEntityCreator.createCourierEntity();
		thirdTestCourier = CourierEntity.create(
			"테스트",
			"example@test.com",
			"test",
			Gender.MALE
		);

		userRepository.save(testUser);
		courierRepository.save(testCourier);
		courierRepository.save(secondTestCourier);
		courierRepository.save(thirdTestCourier);
	}

	@Test
	void 회원_목록_조회_성공() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(get("/api/admin/accounts")
			.param("page", "1")
			.param("size", "10")
			.param("role", AccountRole.MEMBER.name())
			.param("userStatus", "")
			.param("courierWorkStatus", "")
			.param("searchKeyword", "")
			.with(user(userDetails))
		);

		// then
		MvcResult result = actions.andDo(print()).andExpectAll(
			status().isOk(),
			jsonPath("$.code").value("ok"),
			jsonPath("$.message").value("성공"),
			jsonPath("$.data").exists(),
			jsonPath("$.data.totalCount").value(1),
			jsonPath("$.data.totalPages").value(1)
		).andReturn();

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

	@Test
	void 기사_목록_조회_성공() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(get("/api/admin/accounts")
			.param("page", "1")
			.param("size", "10")
			.param("role", AccountRole.COURIER.name())
			.param("userStatus", "")
			.param("courierWorkStatus", "")
			.param("searchKeyword", "테스트")
			.with(user(userDetails))
		);

		// then
		MvcResult result = actions.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.totalCount").value(1),
				jsonPath("$.data.totalPages").value(1)
			)
			.andReturn();

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

}
