package com.kt.api.courier;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CourierEntityCreator;
import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.entity.CourierEntity;
import com.kt.repository.courier.CourierRepository;

@DisplayName("배송기사 상세 조회 - GET /api/couriers/{courierId}")
public class CourierDetailTest extends MockMvcTest {
	@Autowired
	CourierRepository courierRepository;

	CourierEntity testCourier;
	CourierEntity testCourier2;
	@BeforeEach
	void setUp() throws Exception {
		courierRepository.deleteAll();
		testCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier);
		testCourier2 = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier2);
	}

	@Test
	void 배송기사조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/couriers/{courierId}", testCourier.getId())
				.with(user(CurrentUserCreator.getCourierUserDetails(testCourier.getId())))
		);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(testCourier.getId().toString()))
			.andExpect(jsonPath("$.data.email").value(testCourier.getEmail().toString()));
	}

	@Test
	void 배송기사조회_실패__403_다른계정() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/couriers/{courierId}", testCourier.getId())
				.with(user(CurrentUserCreator.getCourierUserDetails(testCourier2.getId())))
		).andExpect(status().isForbidden());
	}
}
