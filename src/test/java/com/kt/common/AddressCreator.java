package com.kt.common;

import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;

public class AddressCreator {
	public static AddressEntity create(UserEntity user) {
		return AddressEntity.create(
			"수령인",
			"01012345678",
			"서울",
			"강남",
			"테헤란로",
			"101호",
			user
		);
	}
}

