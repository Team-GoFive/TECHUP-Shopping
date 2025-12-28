package com.kt.common;

import com.kt.constant.Gender;
import com.kt.domain.entity.SellerEntity;

public class SellerEntityCreator {

	public static SellerEntity createSeller(String email) {
		return SellerEntity.create(
			"판매자1",
			email,
			"$2a$10$HOmkYePSaqAHzPT5DkwwiuEZt4uBGEbo24aefvDSc9yx74PzUkDPW",
			"상점1",
			"010-1234-5678",
			Gender.MALE
		);
	}

	public static SellerEntity createSeller() {
		return SellerEntity.create(
			"판매자1",
			"seller@test.com",
			"$2a$10$HOmkYePSaqAHzPT5DkwwiuEZt4uBGEbo24aefvDSc9yx74PzUkDPW",
			"상점1",
			"010-1234-5678",
			Gender.MALE
		);
	}
}
