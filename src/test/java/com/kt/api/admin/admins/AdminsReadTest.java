package com.kt.api.admin.admins;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

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
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 목록 조회 (어드민) - GET /api/admin/admins")
public class AdminsReadTest extends MockMvcTest {
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

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}

	private DefaultCurrentUser adminPrincipal() {
		return new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);
	}

	@Test
	void 관리자_목록_조회_성공() throws Exception {

		MvcResult result = mockMvc.perform(
				get("/api/admin/admins")
					.param("page", "1")
					.param("size", "10")
					.param("role", UserRole.ADMIN.name())
					.param("userStatus", "")
					.param("courierWorkStatus", "")
					.param("searchKeyword", "")
					.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.totalCount").value(1),
				jsonPath("$.data.totalPages").value(1)
			).andReturn();

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);

	}

}
