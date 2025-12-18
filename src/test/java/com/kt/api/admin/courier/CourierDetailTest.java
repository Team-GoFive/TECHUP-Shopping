package com.kt.api.admin.courier;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kt.common.AdminCreator;
import com.kt.domain.entity.AdminEntity;

import com.kt.repository.admin.AdminRepository;

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
	DefaultCurrentUser adminDetails;
	@Autowired
	CourierRepository courierRepository;
	@Autowired
	AdminRepository adminRepository;
	CourierEntity testCourier;

	@BeforeEach
	void setUp() throws Exception {
		courierRepository.deleteAll();
		testCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier);
		AdminEntity testAdmin = AdminCreator.create();
		adminRepository.save(testAdmin);
		adminDetails  = CurrentUserCreator.getAdminUserDetails(testAdmin.getId());
	}

	@Test
	void 배송기사조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/couriers/{courierId}", testCourier.getId())
				.with(user(adminDetails))
		);

		// then
		actions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(testCourier.getId().toString()))
			.andExpect(jsonPath("$.data.email").value(testCourier.getEmail().toString()));
	}

	@Test
	void 배송기사조회_실패__관리자_아님_403_FORBIDDEN() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/admin/couriers/{courierId}", testCourier.getId())
				.with(user(CurrentUserCreator.getCourierUserDetails(testCourier.getId())))
		);

		// then
		actions.andExpect(status().isForbidden());
	}
}
