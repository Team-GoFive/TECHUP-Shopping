package com.kt.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CourierServiceTest {

	@Autowired
	CourierService courierService;
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
	void 배송기사정보수정_성공(){
		// given
		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		// when
		courierService.updateDetail(testCourier.getId(), update);

		// then
		Assertions.assertEquals(testCourier.getName(),update.name());
	}

	@Test
	void 배송기사조회_성공(){
		// when
		CourierResponse.Detail savedCourier = courierService.getDetail(testCourier.getId());

		// then
		assertThat(savedCourier).satisfies(
			courierEntity -> {
				Assertions.assertEquals(courierEntity.id(),testCourier.getId());
					Assertions.assertEquals(courierEntity.email(),testCourier.getEmail());
			});
	}

}