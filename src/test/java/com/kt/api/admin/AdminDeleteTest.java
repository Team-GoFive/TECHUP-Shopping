package com.kt.api.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 삭제 (어드민) - DELETE /api/admin/{adminId}")
public class AdminDeleteTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;

	DefaultCurrentUser adminDetails;
	UserEntity testAdmin;
	UserEntity testAdmin2;
	UserEntity testUser;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.createMember();
		userRepository.save(testUser);
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
		testAdmin2 = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin2);
		adminDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 관리자_본인_삭제_성공__200_OK() throws Exception {

		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/{adminId}", testAdmin.getId())
			.with(user(adminDetails))
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
		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/{adminId}", UUID.randomUUID())
				.with(user(adminDetails))
			);

		// then
		actions.andExpect(status().isNotFound());
	}

	@Test
	void 관리자_삭제_실패__다른관리자계정에서_시도_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/{adminId}", testAdmin2.getId())
				.with(user(adminDetails))
			);

		// then
		actions.andExpect(status().isForbidden());
	}

	@Test
	void 관리자_삭제_실패__일반계정에서_시도_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/{adminId}", testAdmin2.getId())
				.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
			);

		// then
		actions.andExpect(status().isForbidden());
	}
}
