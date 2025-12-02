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
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
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
	private final DefaultCurrentUser userDetails = CurrentUserCreator.getAdminUserDetails();
	@Autowired
	UserRepository userRepository;

	UserEntity testAdmin;

	@BeforeEach
	void setUp() {
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
	}

	@Test
	void 관리자_삭제_성공() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(delete("/api/admins/{adminId}", testAdmin.getId())
			.with(user(userDetails))
		);

		// then
		MvcResult result =
			actions.andDo(print())
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
				.with(user(userDetails))
			)
			.andDo(print())
			.andExpectAll(
				status().isNotFound());
	}
}
