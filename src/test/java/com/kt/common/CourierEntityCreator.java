package com.kt.common;

import com.kt.constant.Gender;
import com.kt.domain.entity.CourierEntity;

public class CourierEntityCreator {
	public static CourierEntity createCourierEntity() {
		return CourierEntity.create(
			"배송기사테스터",
			"courier@naver.com",
			"wjd747",
			Gender.MALE
		);
	}
}
