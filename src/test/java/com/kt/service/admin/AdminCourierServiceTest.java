package com.kt.service.admin;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.kt.common.AdminCreator;
import com.kt.domain.entity.AdminEntity;

import com.kt.repository.admin.AdminRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CourierEntityCreator;
import com.kt.constant.Gender;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.domain.entity.CourierEntity;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class AdminCourierServiceTest {

	@Autowired
	AdminCourierService adminCourierService;

	@Autowired
	CourierRepository courierRepository;

	@Autowired
	AdminRepository adminRepository;

	CourierEntity testCourier;
	AdminEntity testAdmin;

	@BeforeEach
	void setUp() throws Exception {
		testCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier);
		testAdmin = AdminCreator.create();
		adminRepository.save(testAdmin);
	}

	@Test
	void 배송기사정보수정_어드민_성공() {
		// given
		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		// when
		adminCourierService.updateDetail(testAdmin.getId(), testCourier.getId(), update);

		// then
		Assertions.assertEquals(testCourier.getName(), update.name());
	}

	@Test
	void 배송기사조회_어드민_성공() {
		// when
		CourierResponse.DetailAdmin savedCourier = adminCourierService.getDetail(testCourier.getId(), testCourier.getId());

		// then
		assertThat(savedCourier).satisfies(
			courierEntity -> {
				Assertions.assertEquals(courierEntity.id(), testCourier.getId());
				Assertions.assertEquals(courierEntity.email(), testCourier.getEmail());
			});
	}

}