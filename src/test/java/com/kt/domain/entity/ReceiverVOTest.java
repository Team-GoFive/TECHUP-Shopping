package com.kt.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class ReceiverVOTest {

	@Test
	void 객체생성_성공(){

		ReceiverVO comparisonReceiverVO = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);

		ReceiverVO subjectReceiver = new ReceiverVO(
			"수신자테스터1",
			"010-1234-5678",
			"강원도",
			"원주시",
			"행구로",
			"주소설명"
		);

		assertThat(subjectReceiver).isNotNull();

		assertThat(subjectReceiver)
			.usingRecursiveComparison()
			.ignoringFields("detail")
			.isEqualTo(comparisonReceiverVO);
	}
}