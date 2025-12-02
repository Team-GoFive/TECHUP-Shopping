package com.kt.api.admin.admins;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.MemberRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 생성 (어드민) - POST /api/admins")
public class AdminsCreateTest extends MockMvcTest {
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
				post("/api/admins")
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

}
