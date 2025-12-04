package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;

@ActiveProfiles("test")
class UserEntityTest {

	UserEntity comparisonUser;

	@BeforeEach
	void setUp(){
		comparisonUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);
	}

	@Test
	void 객체생성_성공(){
		UserEntity subjectUser = UserEntity.create(
			"주문자테스터1",
			"wjd123@naver.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			"010-1234-5678"
		);

		assertThat(subjectUser).isNotNull();

		assertThat(subjectUser)
			.usingRecursiveComparison()
			.ignoringFields(
				"role", "birth", "name", "mobile"
			).isEqualTo(comparisonUser);
	}
}