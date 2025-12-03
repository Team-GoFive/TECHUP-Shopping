package com.kt.api.addresses;

import static com.kt.common.UserEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.AddressCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("주소 생성 - POST /api/addresses")
class AddressCreateTest {

	private final String URL = "/api/addresses";
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	UserRepository userRepository;
	UserEntity testMember;
	AddressEntity address;
	AddressRequest validRequest;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		address = addressRepository.save(AddressCreator.createAddress(testMember));

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

		mockMvc.perform(post(URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest))
				.with(SecurityMockMvcRequestPostProcessors.user(
					CurrentUserCreator.getMemberUserDetails(testMember.getId())
				)))
			.andExpect(status().isOk());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("주소_생성_실패__receiverName_null")
	void 주소_생성_실패__receiverName_null(String invalid) throws Exception {

		AddressRequest address = new AddressRequest(
			invalid,
			validRequest.receiverMobile(),
			validRequest.city(),
			validRequest.district(),
			validRequest.roadAddress(),
			validRequest.detail()
		);

		mockMvc.perform(post(URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(address))
				.with(SecurityMockMvcRequestPostProcessors.user(CurrentUserCreator.getMemberUserDetails()
				)))
			.andExpect(status().isBadRequest());
	}

}
