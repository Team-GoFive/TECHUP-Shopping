package com.kt.common;

import com.kt.constant.Gender;
import com.kt.domain.entity.AdminEntity;

public final class AdminCreator {

	static final String password = "123123!@";

	public static AdminEntity create(String name, String email) {
		return AdminEntity.create(
			name,
			email,
			password,
			Gender.MALE
		);
	}

	public static AdminEntity create() {
		return AdminEntity.create(
			"테스트어드민",
			"admin@test.com",
			password,
			Gender.MALE
		);
	}
}
