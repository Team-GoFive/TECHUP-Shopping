package com.kt.common;

import java.time.LocalDate;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.domain.entity.UserEntity;

public class UserEntityCreator {

	public static UserEntity create(String email, String encodedPassword) {
		return UserEntity.create(
			"테스트유저",
			email,
			encodedPassword,
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}

	public static UserEntity create() {
		return UserEntity.create(
			"테스트유저",
			"member@test.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}
}