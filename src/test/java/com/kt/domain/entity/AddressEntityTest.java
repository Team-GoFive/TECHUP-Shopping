package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;

@ActiveProfiles("test")
class AddressEntityTest {

	UserEntity testUser;

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
	}
	@Test
	void 객체생성_성공(){
		AddressEntity comparisonAddress = AddressEntity.create(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명",
			testUser
		);

		AddressEntity subjectAddress = AddressEntity.create(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명",
			testUser
		);

		assertThat(subjectAddress).isNotNull();
		assertThat(subjectAddress)
			.usingRecursiveComparison()
			.ignoringFields()
			.isEqualTo(comparisonAddress);
	}
}