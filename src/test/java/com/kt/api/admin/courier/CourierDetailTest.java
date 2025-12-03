package com.kt.api.admin.courier;

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
import com.kt.security.DefaultCurrentUser;

@DisplayName("배송기사 상세 조회 (어드민) - GET /api/admin/couriers/{courierId}")
public class CourierDetailTest extends MockMvcTest {
	private final DefaultCurrentUser userDetails = CurrentUserCreator.getAdminUserDetails();
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
	void 배송기사조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/couriers/{courierId}", testCourier.getId())
				.with(user(userDetails))
		);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(testCourier.getId().toString()))
			.andExpect(jsonPath("$.data.email").value(testCourier.getEmail().toString()));
	}
}
