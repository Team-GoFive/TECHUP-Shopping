package com.kt.api.courier;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CourierEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.constant.Gender;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.entity.CourierEntity;
import com.kt.repository.courier.CourierRepository;

@DisplayName("배송기사 정보 수정 - PUT /api/couriers/{courierId}")
public class CourierUpdateDetailTest extends MockMvcTest {
	@Autowired
	CourierRepository courierRepository;

	CourierEntity testCourier;
	@BeforeEach
	void setUp() throws Exception {
		courierRepository.deleteAll();
		testCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier);
	}

	@Test
	void 배송기사수정_성공__200_OK() throws Exception {
		// given
		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		String updateJson = objectMapper.writeValueAsString(update);

		// when
		ResultActions actions = mockMvc.perform(
			put("/api/couriers/{courierId}", testCourier.getId())
				.with(user(CurrentUserCreator.getMemberUserDetails()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson)
		);

		// then
		actions.andExpect(status().isOk());
		Assertions.assertEquals(testCourier.getName(),update.name());
	}
}
