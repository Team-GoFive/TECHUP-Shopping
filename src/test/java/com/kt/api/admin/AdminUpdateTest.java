package com.kt.api.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.UUID;

import com.kt.common.AdminCreator;

import com.kt.domain.dto.request.AdminRequest;
import com.kt.domain.entity.AdminEntity;

import com.kt.repository.admin.AdminRepository;

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
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("관리자 정보 수정 (어드민) - PUT /api/admin")
public class AdminUpdateTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	AdminRepository adminRepository;
	DefaultCurrentUser adminsDetails;
	AdminEntity testAdmin;
	UserEntity testUser;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		testAdmin = AdminCreator.create();
		adminRepository.save(testAdmin);
		adminsDetails = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 관리자_업데이트_성공__200_OK() throws Exception {

		// given
		var requset = new AdminRequest.Update(
			"어드민_업데이트",
			"admin_update@test.com"
		);

		// when
		ResultActions actions = mockMvc.perform(
			put("/api/admin")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requset))
			.with(user(adminsDetails))
		);

		MvcResult result = actions.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공")
			).andReturn();

		AdminEntity updatedAdmin = adminRepository.findByAdminCode(AdminEntity.SYSTEM_ADMIN_CODE);

		assertThat(updatedAdmin.getName()).isEqualTo("어드민_업데이트");
		String responseJson = result.getResponse().getContentAsString();
		log.info("response : {}", responseJson);
	}

	@Test
	void 관리자_업데이트_실패__404_NotFound() throws Exception {
		// given
		AdminRequest.Update requset = new AdminRequest.Update(
			"김도현",
			"test_123@test.com"
		);

		// when
		ResultActions actions = mockMvc.perform(
			put("/api/admin/{adminId}", UUID.randomUUID())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requset))
			.with(user(adminsDetails))
		);

		// then
		actions.andExpect(status().isNotFound());
	}

	@Test
	void 관리자_업데이트_실패__일반_유저계정에서_시도_403_FORBIDDEN() throws Exception {
		// given
		AdminRequest.Update request = new AdminRequest.Update(
			"김도현",
			"test_123@test.com"
		);

		// when
		ResultActions actions = mockMvc.perform(
			put("/api/admin", testAdmin.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
			.with(user(CurrentUserCreator.getMemberUserDetails(testUser.getId())))
		);

		// then
		actions.andExpect(status().isForbidden());
	}
}
