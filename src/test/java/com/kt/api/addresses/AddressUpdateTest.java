package com.kt.api.addresses;

import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kt.common.UserEntityCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;

@DisplayName("주소 수정 - PUT /api/addresses/{addressId}")
class AddressUpdateTest extends MockMvcTest {

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
	void 주소_수정_성공__정상입력() throws Exception {
		// given
		AddressEntity address = AddressCreator.createAddress(testUser);
		addressRepository.save(address);

		//when
		AddressRequest request = new AddressRequest(
			"새이름",
			"01022223333",
			"부산시",
			"해운대구",
			"센텀로",
			"202호"
		);

		ResultActions actions = mockMvc.perform(
			put("/api/addresses/{addressId}", address.getId())
				.with(user(getMemberUserDetails(testUser.getEmail())))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);
		// then
		actions.andExpect(status().isOk());
	}

	@Nested
	@DisplayName("주소_수정_실패__검증")
	class ValidationTests {

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주소_수정_실패__receiverName_null_or_blank")
		void 주소_수정_실패__receiverName_null_or_blank(String invalid) throws Exception {
			runInvalidTest("receiverName", invalid);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주소_수정_실패__city_null_or_blank")
		void 주소_수정_실패__city_null_or_blank(String invalid) throws Exception {
			runInvalidTest("city", invalid);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주소_수정_실패__district_null_or_blank")
		void 주소_수정_실패__district_null_or_blank(String invalid) throws Exception {
			runInvalidTest("district", invalid);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주소_수정_실패__roadAddress_null_or_blank")
		void 주소_수정_실패__roadAddress_null_or_blank(String invalid) throws Exception {
			runInvalidTest("roadAddress", invalid);
		}

		private void runInvalidTest(String field, String invalid) throws Exception {
			UserEntity testUser = UserEntityCreator.create();
			userRepository.save(testUser);

			AddressEntity address = addressRepository.save(
				AddressEntity.create(
					"받는사람",
					"01011112222",
					"서울시",
					"강남구",
					"테헤란로",
					"101호",
					testUser
				)
			);

			var currentUser = getMemberUserDetails(testUser.getId());

			AddressRequest addressRequest = new AddressRequest(
				field.equals("receiverName") ? invalid : "받는사람",
				"01011112222",
				field.equals("city") ? invalid : "서울시",
				field.equals("district") ? invalid : "강남구",
				field.equals("roadAddress") ? invalid : "테헤란로",
				"101호"
			);

			mockMvc.perform(
					put("/api/addresses/{addressId}", address.getId())
						.with(user(currentUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(addressRequest))
				)
				.andExpect(status().isBadRequest());
		}
	}
}
