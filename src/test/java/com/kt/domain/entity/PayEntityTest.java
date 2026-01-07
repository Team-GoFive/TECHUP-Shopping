package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
public class PayEntityTest {

	@Test
	void pay_객체생성_성공() {
		UserEntity testUser = UserEntityCreator.create();
		PayEntity pay = testUser.getPay();
		assertNotNull(pay);
		log.info("tesetUserName : {}, pay.getUser() : {}",
			testUser.getName(), pay.getUser().getName());
		assertEquals(pay.getUser(), testUser);

	}
}
