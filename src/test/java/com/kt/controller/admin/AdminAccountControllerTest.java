package com.kt.controller.admin;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.UUID;

import com.kt.constant.AccountRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
class AdminAccountControllerTest extends MockMvcTest {

	static final String TEST_PASSWORD = "1234561111";
	UserEntity testUser;
	CourierEntity testCourier;
	CourierEntity secondTestCourier;
	CourierEntity thirdTestCourier;
	UserEntity testAdmin;
	DefaultCurrentUser authority;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CourierRepository courierRepository;
	@Autowired
	private AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		testAdmin = UserEntity.create(
			"테스트관리자1",
			"admintest@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			AccountRole.ADMIN,
			Gender.MALE,
			LocalDate.of(1999, 1, 1),
			"01012340001"
		);
		userRepository.save(testAdmin);

		testUser = UserEntity.create(
			"테스트유저1",
			"usertest@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.of(2000, 1, 1),
			"01012340002"
		);
		userRepository.save(testUser);

		testCourier = CourierEntity.create(
			"테스트기사1",
			"couriertest@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);

		secondTestCourier = CourierEntity.create(
			"최첨지",
			"couriertest123@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);

		thirdTestCourier = CourierEntity.create(
			"김테스트",
			"couriertest3451@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			Gender.MALE
		);
		courierRepository.save(testCourier);
		courierRepository.save(secondTestCourier);
		courierRepository.save(thirdTestCourier);

		setDefaultCurrentUser();
	}

	void setDefaultCurrentUser() {
		authority = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			AccountRole.ADMIN
		);
	}

	@Test
	void 회원_목록_조회_성공() throws Exception {

		ResultActions actions = mockMvc.perform(get("/api/admin/accounts")
			.param("page", "1")
			.param("size", "10")
			.param("role", AccountRole.MEMBER.name())
			.param("userStatus", "")
			.param("courierWorkStatus", "")
			.param("searchKeyword", "")
			.with(user(authority))
		).andDo(print());

		actions.andExpectAll(
			status().isOk(),
			jsonPath("$.code").value("ok"),
			jsonPath("$.message").value("성공"),
			jsonPath("$.data").exists(),
			jsonPath("$.data.totalCount").value(1),
			jsonPath("$.data.totalPages").value(1)
		);
	}

	@Test
	void 기사_목록_조회_성공() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/admin/accounts")
				.param("page", "1")
				.param("size", "10")
				.param("role", AccountRole.COURIER.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "테스트")
				.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.totalCount").value(2),
				jsonPath("$.data.totalPages").value(1)
			)
			.andReturn();

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

	@Test
	void 회원_상세_조회_성공() throws Exception {
		MvcResult result = mockMvc.perform(
				get("/api/admin/users/{userId}", testUser.getId())
					.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.id").value(testUser.getId().toString()),
				jsonPath("$.data.email").value(testUser.getEmail())
			).andReturn();

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

	@Test
	void 회원_활성화_성공() throws Exception {
		// given
		testUser.disabled();

		// when
		MvcResult result = mockMvc.perform(
				patch("/api/admin/users/{userId}/enabled", testUser.getId())
					.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		// then
		UserEntity savedUser = userRepository.findByIdOrThrow(testUser.getId());
		assertEquals(UserStatus.ENABLED, savedUser.getStatus());

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 회원_비활성화_성공() throws Exception {

		// when
		MvcResult result = mockMvc.perform(
				patch("/api/admin/users/{userId}/disabled", testUser.getId())
					.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		// then
		UserEntity savedUser = userRepository.findByIdOrThrow(testUser.getId());
		assertEquals(UserStatus.DISABLED, savedUser.getStatus());

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 계정_soft_삭제_성공() throws Exception {
		MvcResult result = mockMvc.perform(
				delete("/api/admin/accounts/{accountId}", testUser.getId())
					.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		UserEntity savedUser = userRepository.findByIdOrThrow(testUser.getId());
		assertEquals(UserStatus.DELETED, savedUser.getStatus());

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 계정_hard_삭제_성공() throws Exception {
		UUID accountId = testUser.getId();
		MvcResult result = mockMvc.perform(
				delete("/api/admin/accounts/{accountId}/force", accountId)
					.with(user(authority))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		assertThatThrownBy(() -> accountRepository.findByIdOrThrow(accountId))
			.isInstanceOf(CustomException.class);

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

}
