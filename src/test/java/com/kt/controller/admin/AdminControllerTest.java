package com.kt.controller.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.domain.dto.request.MemberRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {
	@Autowired MockMvc mockMvc;
	@Autowired UserRepository userRepository;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired ObjectMapper objectMapper;

	static final String TEST_PASSWORD = "admin12345";

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
	void cleanUp() {
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

	@Test
	void 관리자_상세_조회_성공() throws Exception {

		MvcResult result = mockMvc.perform(get(
			"/api/admin/admins/{adminId}", testAdmin.getId()
				).with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.id").value(testAdmin.getId().toString()),
				jsonPath("$.data.email").value(testAdmin.getEmail())
			).andReturn();

		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_생성_성공() throws Exception {

		var request = new MemberRequest.SignupMember(
			"테스트어드민",
			"test@examlple.com",
			"1234",
			Gender.MALE,
			LocalDate.now(),
			"010-1111-1111"
		);

		MvcResult result = mockMvc.perform(
			post("/api/admin/admins")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		assertThat(userRepository.findByEmail("test@examlple.com")).isPresent();
		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_업데이트_성공() throws Exception {

		var requset = new UserRequest.UpdateDetails(
			"김도현",
			"010-1234-1234",
			LocalDate.now(),
			Gender.FEMALE
		);

		MvcResult result = mockMvc.perform(put("/api/admin/admins/{adminId}", testAdmin.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requset))
				.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		UserEntity foundedUser = userRepository.findByIdOrThrow(testAdmin.getId());

		assertThat(foundedUser.getName()).isEqualTo("김도현");
		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_삭제_성공() throws Exception {

		MvcResult result = mockMvc.perform(delete("/api/admin/admins/{adminId}", testAdmin.getId())
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
}
