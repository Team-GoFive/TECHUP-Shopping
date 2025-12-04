package com.kt.api.addresses;

import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;

@DisplayName("주소 삭제 - DELETE /api/addresses")
class AddressDeleteTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	UserEntity testMember;

	@BeforeEach
	void setUp() {
		testMember = UserEntityCreator.createMember();
		userRepository.save(testMember);
	}

	@Test
	void 주소_삭제_실패__주소_존재하지_않음() throws Exception {
		//when
		ResultActions actions = mockMvc.perform(
			delete("/api/addresses/{addressId}", UUID.randomUUID())
				.with(user(getMemberUserDetails(testMember.getEmail())))
		);

		// then
		actions.andExpect(status().isNotFound());
	}
}
