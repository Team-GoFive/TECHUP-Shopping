package com.kt.common;

import com.kt.domain.entity.ReceiverVO;

public class ReceiverCreator {
	public static ReceiverVO createReceiver(){
		return new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);
	}
}
