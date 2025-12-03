package com.kt.api.addresses;

import static com.kt.common.UserEntityCreator.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.AddressCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("주소 조회 - GET /api/addresses")
class AddressSearchTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AddressRepository addressRepository;

	UserEntity user;

	@BeforeEach
	void setUp() {
		user = createMember();
		userRepository.save(user);
	}

	@Test
	@DisplayName("주소_목록조회_성공__주소없을때_빈리스트_반환")
	void 주소_조회_성공__빈리스트() throws Exception {

		mockMvc.perform(get("/api/addresses")
				.with(SecurityMockMvcRequestPostProcessors.user(
					CurrentUserCreator.getMemberUserDetails(user.getId())
				)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()").value(0));
	}

	@DisplayName("주소_목록조회_성공__주소2개_반환")
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
			user
		);

		AddressEntity address2 = AddressEntity.create(
			"받는사람2",
			"010-0000-2222",
			"부산시",
			"해운대구",
			"센텀로 2",
			"202호",
			user
		);
		addressRepository.save(address1);
		addressRepository.save(address2);

		var currentUser = CurrentUserCreator.getMemberUserDetails(user.getId());

		// when & then
		mockMvc.perform(
				get("/api/addresses")
					.with(SecurityMockMvcRequestPostProcessors.user(currentUser))
			)
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[*].receiverName").value(
				Matchers.containsInAnyOrder("받는사람1", "받는사람2")));
	}
}
