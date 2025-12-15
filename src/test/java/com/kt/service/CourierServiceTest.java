package com.kt.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.CourierEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.Gender;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.domain.entity.CourierEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.courier.CourierRepository;
import com.kt.repository.user.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CourierServiceTest {

	@Autowired
	CourierService courierService;
	@Autowired
	CourierRepository courierRepository;
	@Autowired
	UserRepository userRepository;

	CourierEntity testCourier;
	UserEntity testAdmin;

	@BeforeEach
	void setUp() throws Exception {
		testCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(testCourier);
		testAdmin = UserEntityCreator.createAdmin();
		userRepository.save(testAdmin);
	}

	@Test
	void 배송기사정보수정_본인_성공() {
		// given
		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		// when
		courierService.updateDetail(testCourier.getId(), testCourier.getId(), update);

		// then
		Assertions.assertEquals(testCourier.getName(), update.name());
	}

	@Test
	void 배송기사정보수정_어드민_성공() {
		// given
		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		// when
		courierService.updateDetail(testAdmin.getId(), testCourier.getId(), update);

		// then
		Assertions.assertEquals(testCourier.getName(), update.name());
	}

	@Test
	void 배송기사정보수정_실패__본인아님() {
		// given
		CourierEntity someCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(someCourier);

		CourierRequest.UpdateDetails update = new CourierRequest.UpdateDetails(
			"변경된 테스터명",
			Gender.FEMALE
		);

		// then
		assertThatThrownBy(
			() -> courierService.updateDetail(someCourier.getId(), testCourier.getId(), update)
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}

	@Test
	void 배송기사조회_성공_본인() {
		// when
		CourierResponse.Detail savedCourier = courierService.getDetail(testCourier.getId(), testCourier.getId());

		// then
		assertThat(savedCourier).satisfies(
			courierEntity -> {
				Assertions.assertEquals(courierEntity.id(), testCourier.getId());
				Assertions.assertEquals(courierEntity.email(), testCourier.getEmail());
			});
	}

	@Test
	void 배송기사조회_실패_본인_아님() {
		// given
		CourierEntity someCourier = CourierEntityCreator.createCourierEntity();
		courierRepository.save(someCourier);

		// then
		assertThatThrownBy(
			() -> courierService.getDetail(someCourier.getId(), testCourier.getId())
		)
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED.name());
	}
}