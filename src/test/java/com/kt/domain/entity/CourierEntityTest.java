package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;

@ActiveProfiles("test")
class CourierEntityTest {

	@Test
	void 객체생성_성공(){
		CourierEntity comparisonEntity = CourierEntity.create(
			"테스터1",
			"wjd1234@naver.com",
			"1234",
			Gender.MALE
		);

		CourierEntity subjectEntity = CourierEntity.create(
			"테스터1",
			"wjd1234@naver.com",
			"1234",
			Gender.MALE
		);

		assertThat(subjectEntity).isNotNull();

		assertThat(subjectEntity)
			.usingRecursiveComparison()
			.ignoringFields()
			.isEqualTo(comparisonEntity);
	}
}