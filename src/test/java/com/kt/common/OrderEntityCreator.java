package com.kt.common;

import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ReceiverVO;
import com.kt.domain.entity.UserEntity;

public class OrderEntityCreator {

	public static OrderEntity createOrderEntity(UserEntity user) {
		ReceiverVO receiverVO = ReceiverVO.create(
			"test-receiver",
			"010-1213-1232",
			"서울",
			"강남구",
			"테스트로",
			"101동 1001호"
		);

		return OrderEntity.create(receiverVO, user);
	}

	public static OrderEntity createOrderEntity(UserEntity user, ReceiverVO receiverVO) {
		return OrderEntity.create(receiverVO, user);
	}

}
