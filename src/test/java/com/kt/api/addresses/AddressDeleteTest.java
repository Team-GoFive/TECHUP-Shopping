package com.kt.api.addresses;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.kt.common.CurrentUserCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주소 삭제 - DELETE /api/addresses")
class AddressDeleteTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;

	UserEntity user;

	@BeforeEach
	void setUp() throws Exception {
		user = userRepository.save(UserEntityCreator.createMember());
	}

	@Test
	@DisplayName("주소_삭제_실패__주소_존재하지_않음")
	void 주소_삭제_실패__주소없음() throws Exception {

		UUID id = UUID.randomUUID();

		mockMvc.perform(delete("/api/addresses/" + id)
				.with(SecurityMockMvcRequestPostProcessors.user(
					CurrentUserCreator.getMemberUserDetails(user.getId())
				)))
			.andExpect(status().isNotFound());
	}
}
