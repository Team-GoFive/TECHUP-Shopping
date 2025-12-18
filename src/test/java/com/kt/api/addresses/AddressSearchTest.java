package com.kt.api.addresses;

import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kt.common.UserEntityCreator;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

@DisplayName("주소 조회 - GET /api/addresses")
class AddressSearchTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	AddressRepository addressRepository;

	UserEntity testUser;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
	}

	@Test
	void 주소_목록조회_성공__주소없을때_빈리스트_반환() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(get("/api/addresses")
			.with(user(getMemberUserDetails(testUser.getEmail())))
		);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(0));
	}

	@Test
	void 주소_목록조회_성공__주소2개_정상반환() throws Exception {

		// given
		AddressEntity address1 = AddressEntity.create(
			"받는사람1",
			"010-0000-1111",
			"서울시",
			"강남구",
			"테헤란로 1",
			"101호",
			testUser
		);
		addressRepository.save(address1);

		AddressEntity address2 = AddressEntity.create(
			"받는사람2",
			"010-0000-2222",
			"부산시",
			"해운대구",
			"센텀로 2",
			"202호",
			testUser
		);
		addressRepository.save(address2);

		// when
		ResultActions actions = mockMvc.perform(
			get("/api/addresses")
				.with(user(getMemberUserDetails(testUser.getEmail())))
		);

		// then
		actions
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[*].receiverName").value(
				Matchers.containsInAnyOrder("받는사람1", "받는사람2")));
	}
}
