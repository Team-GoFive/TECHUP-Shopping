package com.kt.api.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.UUID;

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
@DisplayName("관리자 삭제 (어드민) - DELETE /api/admins/{adminId}")
public class AdminDeleteTest extends MockMvcTest {
	static final String TEST_PASSWORD = "admin12345";
	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	UserEntity testAdmin;

	@BeforeEach
	void setUp() {
		testAdmin = UserEntity.create(
			"테스트관리자1",
			"test@example.com",
			passwordEncoder.encode(TEST_PASSWORD),
			UserRole.ADMIN,
			Gender.MALE,
			LocalDate.now(),
			"010-1231-1212"
		);
		userRepository.save(testAdmin);
	}

	private DefaultCurrentUser adminPrincipal() {
		return new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);
	}

	@Test
	void 관리자_삭제_성공() throws Exception {

		MvcResult result = mockMvc.perform(delete("/api/admins/{adminId}", testAdmin.getId())
				.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		UserEntity foundedUser = userRepository.findByIdOrThrow(testAdmin.getId());
		assertThat(foundedUser.getStatus()).isEqualTo(UserStatus.DELETED);

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_삭제_실패__404_NotFound() throws Exception {

		mockMvc.perform(delete("/api/admins/{adminId}", UUID.randomUUID())
				.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}
}
