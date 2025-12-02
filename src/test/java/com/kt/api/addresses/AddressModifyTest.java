package com.kt.api.addresses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.AddressCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("주소 수정 - PUT /api/addresses/{addressId}")
class AddressModifyTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AddressRepository addressRepository;
	@Autowired
	ObjectMapper objectMapper;

	private static final String URL = "/api/addresses/{addressId}";

	@Test
	@DisplayName("주소_수정_성공__정상입력")
	void 주소_수정_성공__정상입력() throws Exception {

		UserEntity user = userRepository.save(UserEntityCreator.createMember());
		AddressEntity address = addressRepository.save(AddressCreator.create(user));
		var currentUser = CurrentUserCreator.getMemberUserDetails(user.getId());

		AddressRequest request = new AddressRequest(
			"새이름",
			"01022223333",
			"부산시",
			"해운대구",
			"센텀로",
			"202호"
		);

		mockMvc.perform(
				put(URL, address.getId())
					.with(SecurityMockMvcRequestPostProcessors.user(currentUser))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk());
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

			UserEntity user = userRepository.save(UserEntityCreator.createMember());

			AddressEntity address = addressRepository.save(
				AddressEntity.create(
					"받는사람",
					"01011112222",
					"서울시",
					"강남구",
					"테헤란로",
					"101호",
					user
				)
			);

			var currentUser = CurrentUserCreator.getMemberUserDetails(user.getId());

			AddressRequest addressRequest = new AddressRequest(
				field.equals("receiverName") ? invalid : "받는사람",
				"01011112222",
				field.equals("city") ? invalid : "서울시",
				field.equals("district") ? invalid : "강남구",
				field.equals("roadAddress") ? invalid : "테헤란로",
				"101호"
			);

			mockMvc.perform(
					put(URL, address.getId())
						.with(SecurityMockMvcRequestPostProcessors.user(currentUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(addressRequest))
				)
				.andExpect(status().isBadRequest());
		}
	}
}
