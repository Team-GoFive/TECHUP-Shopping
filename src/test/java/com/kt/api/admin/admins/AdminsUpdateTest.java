package com.kt.api.admin.admins;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 정보 수정 (어드민) - PUT /api/admins/{adminsId}")
public class AdminsUpdateTest extends MockMvcTest {

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
	void 관리자_업데이트_성공() throws Exception {

		var requset = new UserRequest.UpdateDetails(
			"김도현",
			"010-1234-1234",
			LocalDate.now(),
			Gender.FEMALE
		);

		MvcResult result = mockMvc.perform(put("/api/admins/{adminId}", testAdmin.getId())
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
	void 관리자_업데이트_실패__404_NotFound() throws Exception {

		var requset = new UserRequest.UpdateDetails(
			"김도현",
			"010-1234-1234",
			LocalDate.now(),
			Gender.FEMALE
		);

		mockMvc.perform(put("/api/admins/{adminId}", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requset))
				.with(user(adminPrincipal()))
			)
			.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}

}
