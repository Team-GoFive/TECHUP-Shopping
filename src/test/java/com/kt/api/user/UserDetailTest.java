package com.kt.api.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("내 정보 조회(회원) - GET /api/users")
public class UserDetailTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;

	UserEntity testUser;
	DefaultCurrentUser userDetails;

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		userDetails = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			testUser.getRole()
		);
	}

	@Test
	void 내정보조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/users")
				.with(user(userDetails)));

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(testUser.getId().toString()))
			.andExpect(jsonPath("$.data.email").value(testUser.getEmail().toString()));
	}
}
