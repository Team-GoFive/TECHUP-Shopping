package com.kt.domain.entity;

import com.kt.constant.Gender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class AdminEntityTest {

	@Test
	void 관리자_계정생성_성공() {
		AdminEntity admin = AdminEntity.create(
			"테스트관리자",
			"admin@test.com",
			"123123!@",
			Gender.MALE
		);
		Assertions.assertNotNull(admin);

	}

}
