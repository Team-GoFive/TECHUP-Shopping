package com.kt.api.admin.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 활성화 (어드민)  - PATCH /api/admin/users{userId}/enabled")
public class AdminUserEnabledTest extends MockMvcTest {

	String TEST_PASSWORD = "123456";

	UserEntity testUser;
	UserEntity testAdmin;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {

		testAdmin = UserEntity.create(
			"테스트관리자1",
			"admintest@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			UserRole.ADMIN,
			Gender.MALE,
			LocalDate.of(1999, 1, 1),
			"01012340001"
		);

		testUser = UserEntity.create(
			"테스트유저1",
			"usertest@gmail.com",
			passwordEncoder.encode(TEST_PASSWORD),
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(2000, 1, 1),
			"01012340002"
		);

		userRepository.save(testAdmin);
		userRepository.save(testUser);
	}

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	void 회원_활성화_성공_200() throws Exception {
		// given
		testUser.disabled();

		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		// when
		MvcResult result = mockMvc.perform(
				patch("/api/admin/users/{userId}/enabled", testUser.getId())
					.with(user(admin))
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
	void 회원_활성화_실패_404_NotFound() throws Exception {
		// given

		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		// when
		mockMvc.perform(
				patch("/api/admin/users/{userId}/enabled", UUID.randomUUID())
					.with(user(admin))
			)
			.andDo(print())
			.andExpect(status().isNotFound());
	}

}
