package com.kt.common;

import java.time.LocalDate;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.entity.UserEntity;

public class UserEntityCreator {

	public static UserEntity createMember() {
		return UserEntity.create(
			"회원1",
			"member@test.com",
			"1234",
			UserRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}
}
