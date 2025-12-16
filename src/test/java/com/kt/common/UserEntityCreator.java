package com.kt.common;

import java.time.LocalDate;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.domain.entity.UserEntity;

public class UserEntityCreator {

	public static UserEntity createMember(String email, String encodedPassword) {
		return UserEntity.create(
			"회원1",
			email,
			encodedPassword,
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}

	public static UserEntity createMember() {
		return UserEntity.create(
			"회원1",
			"member@test.com",
			"1234",
			AccountRole.MEMBER,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}

	public static UserEntity createAdmin() {
		return UserEntity.create(
			"관리자1",
			"admin@test.com",
			"1234",
			AccountRole.ADMIN,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}
}