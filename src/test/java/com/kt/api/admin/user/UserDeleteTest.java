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
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 삭제 (어드민) - GET /api/admin/users/{userId}/disabled")
public class UserDeleteTest extends MockMvcTest {

	static final String TEST_PASSWORD = "1234561111";
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
		userRepository.save(testUser);
		userRepository.save(testAdmin);
	}

	@Test
	void 회원_삭제_성공() throws Exception {
		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		MvcResult result = mockMvc.perform(
				patch("/api/admin/users/{userId}/removed", testUser.getId())
					.with(user(admin))
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
	void 회원_삭제_실패__404_NotFound() throws Exception {
		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		mockMvc.perform(
				patch("/api/admin/users/{userId}/removed", UUID.randomUUID())
					.with(user(admin))
			)
			.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}
}
