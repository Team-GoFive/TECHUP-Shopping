package com.kt.common;

import java.time.LocalDate;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.domain.entity.UserEntity;

public class UserEntityCreator {

	public static final String DEFAULT_MEMBER_EMAIL = "member@test.com";

	public static UserEntity createMember() {
		return UserEntity.create(
			"회원1",
			DEFAULT_MEMBER_EMAIL,
			"1234",
			UserRole.MEMBER,
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
			UserRole.ADMIN,
			Gender.MALE,
			LocalDate.now(),
			"010-1234-5678"
		);
	}
}