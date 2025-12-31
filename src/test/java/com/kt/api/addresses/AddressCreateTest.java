package com.kt.api.addresses;

import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

@DisplayName("주소 생성 - POST /api/addresses")
class AddressCreateTest extends MockMvcTest {

	@Autowired
	AddressRepository addressRepository;
	@Autowired
	UserRepository userRepository;

	UserEntity testUser;
	AddressEntity address;
	AddressRequest validRequest;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		address = AddressCreator.createAddress(testUser);
		addressRepository.save(address);

		validRequest = new AddressRequest(
			address.getReceiverName(),
			address.getReceiverMobile(),
			address.getCity(),
			address.getDistrict(),
			address.getRoadAddress(),
			address.getDetail()
		);
	}

	@Test
	@DisplayName("주소_생성_성공")
	void 주소_생성_성공() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			post("/api/addresses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest))
				.with(user(getMemberUserDetails(testUser.getId())))
		);

		// then
		actions.andExpect(status().isOk());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 주소_생성_실패__receiverName_null(String invalid) throws Exception {
		// when
		AddressRequest address = new AddressRequest(
			invalid,
			validRequest.receiverMobile(),
			validRequest.city(),
			validRequest.district(),
			validRequest.roadAddress(),
			validRequest.detail()
		);

		ResultActions actions = mockMvc.perform(
			post("/api/addresses")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(address))
				.with(user(getMemberUserDetails()))
		);

		// then
		actions.andExpect(status().isBadRequest());
	}

}
