package com.kt.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.exception.FieldValidationException;

@ActiveProfiles("test")
class OrderEntityTest {

	UserEntity testUser;
	ReceiverVO testReceiver;

	@BeforeEach
	void setUp() throws Exception {
		testUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		)	;
		testReceiver = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);
	}

	@Test
	void 객체생성_성공() {
		OrderEntity order = OrderEntity.create(
			testReceiver,
			testUser
		);

		Assertions.assertNotNull(order);

		Assertions.assertEquals(order.getReceiverVO(), testReceiver);
		Assertions.assertEquals(order.getOrderBy(), testUser);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_name_null_또는_공백(String name){
			assertThrowsExactly(
				FieldValidationException.class,
				() -> {
					ReceiverVO receiver = new ReceiverVO(
						name,
						"010-1234-5678",
						"강원도",
						"원주시",
						"행구로",
						"주소설명"
					);
					OrderEntity order = OrderEntity.create(
						receiver,
						testUser
					);
				}
			);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_mobile_null_또는_공백(String mobile){
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				ReceiverVO receiver = new ReceiverVO(
					"수신자테스터",
					mobile,
					"강원도",
					"원주시",
					"행구로",
					"주소설명"
				);
				OrderEntity order = OrderEntity.create(
					receiver,
					testUser
				);
			}
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_city_null_또는_공백(String city){
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				ReceiverVO receiver = new ReceiverVO(
					"수신자테스터",
					"010-1234-5678",
					city,
					"원주시",
					"행구로",
					"주소설명"
				);
				OrderEntity order = OrderEntity.create(
					receiver,
					testUser
				);
			}
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_district_null_또는_공백(String district){
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				ReceiverVO receiver = new ReceiverVO(
					"수신자테스터",
					"010-1234-5678",
					"강원도",
					district,
					"행구로",
					"주소설명"
				);
				OrderEntity order = OrderEntity.create(
					receiver,
					testUser
				);
			}
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_roadAddress_null_또는_공백(String road_address){
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				ReceiverVO receiver = new ReceiverVO(
					"수신자테스터",
					"010-1234-5678",
					"강원도",
					"원주시",
					road_address,
					"주소설명"
				);
				OrderEntity order = OrderEntity.create(
					receiver,
					testUser
				);
			}
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 객체생성_실패__Receiver_detail_null_또는_공백(String detail){
		assertThrowsExactly(
			FieldValidationException.class,
			() -> {
				ReceiverVO receiver = new ReceiverVO(
					"수신자테스터",
					"010-1234-5678",
					"강원도",
					"원주시",
					"행구로",
					detail
				);
				OrderEntity order = OrderEntity.create(
					receiver,
					testUser
				);
			}
		);
	}

}