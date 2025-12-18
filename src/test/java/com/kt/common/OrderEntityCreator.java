package com.kt.common;

import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.UserEntity;

public class OrderEntityCreator {

	public static OrderEntity createOrderEntity() {
		ReceiverVO receiverVO = ReceiverVO.create(
			"test",
			"010-1213-1232",
			"테스트",
			"테스트",
			"길",
			"상세"
		);
		UserEntity testUser = UserEntityCreator.create();
		return OrderEntity.create(
			receiverVO,
			testUser
		);
	}

	public static OrderEntity createOrderEntity(UserEntity userEntity) {
		ReceiverVO receiverVO = ReceiverVO.create(
			"test",
			"010-1213-1232",
			"테스트",
			"테스트",
			"길",
			"상세"
		);
		return OrderEntity.create(
			receiverVO,
			userEntity
		);
	}

}
