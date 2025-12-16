package com.kt.api.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.Gender;
import com.kt.domain.dto.request.MemberRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("유저 생성 (어드민) - POST /api/admin")
public class AdminCreateTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;
	UserEntity testAdmin;
	UserEntity testUser;
	DefaultCurrentUser adminDetails;

	@BeforeEach
	void setUp() {
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
		adminDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);
	}

	@Test
	void 관리자_생성_성공() throws Exception {
		// given
		var request = new MemberRequest.SignupMember(
			"테스트어드민",
			"test@examlple.com",
			"1234",
			Gender.MALE,
			LocalDate.now(),
			"010-1111-1111"
		);

		// when
		MvcResult result = mockMvc.perform(
				post("/api/admin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.with(user(adminDetails))
			)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		// then
		assertThat(userRepository.findByEmail("test@examlple.com")).isPresent();
		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_생성_실패__일반계정에서_시도_403_FORBIDDEN() throws Exception {
		// given
		MemberRequest.SignupMember request = new MemberRequest.SignupMember(
			"테스트어드민",
			"test@examlple.com",
			"1234",
			Gender.MALE,
			LocalDate.now(),
			"010-1111-1111"
		);

		// when
		ResultActions actions = mockMvc.perform(
				post("/api/admin")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
					.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
		);

		// then
		actions.andExpect(status().isForbidden());
	}
}
