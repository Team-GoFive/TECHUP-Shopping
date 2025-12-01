package com.kt.api.admin.account;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@Slf4j
@DisplayName("")
class AdminAccountReadTest extends MockMvcTest {

	static final String TEST_PASSWORD = "1234561111";
	UserEntity testUser;
	CourierEntity testCourier;
	CourierEntity secondTestCourier;
	CourierEntity thirdTestCourier;
	UserEntity testAdmin;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CourierRepository courierRepository;

	@BeforeEach
	void setUp() {

		if (userRepository.findByEmail("admintest@gmail.com").isEmpty()) {
			testAdmin = UserEntity.create(
				"테스트관리자1",
				"admintest@gmail.com",
				passwordEncoder.encode(TEST_PASSWORD),
				UserRole.ADMIN,
				Gender.MALE,
				LocalDate.of(1999, 1, 1),
				"01012340001"
			);
			userRepository.save(testAdmin);
		}
		if (userRepository.findByEmail("usertest@gmail.com").isEmpty()) {
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
		}

		if (courierRepository.findAll().isEmpty()) {
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
		}
	}

	@Test
	void 회원_목록_조회_성공() throws Exception {
		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		MvcResult result = mockMvc.perform(get("/api/admin/accounts")
				.param("page", "1")
				.param("size", "10")
				.param("role", UserRole.MEMBER.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "")
				.with(user(admin))
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

		String response = result.getResponse().getContentAsString();
		log.info("response : {}", response);
	}

	@Test
	void 기사_목록_조회_성공() throws Exception {
		DefaultCurrentUser admin = new DefaultCurrentUser(
			testAdmin.getId(),
			testAdmin.getEmail(),
			UserRole.ADMIN
		);

		MvcResult result = mockMvc.perform(get("/api/admin/accounts")
				.param("page", "1")
				.param("size", "10")
				.param("role", UserRole.COURIER.name())
				.param("userStatus", "")
				.param("courierWorkStatus", "")
				.param("searchKeyword", "테스트")
				.with(user(admin))
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

}
