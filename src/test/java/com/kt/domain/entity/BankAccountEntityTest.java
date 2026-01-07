package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
public class BankAccountEntityTest {

	@Test
	void 계좌_객체생성_성공() {
		UserEntity testUser = UserEntityCreator.create();
		BankAccountEntity bankAccount = BankAccountEntity.create(testUser, testUser.getName());

		assertNotNull(bankAccount);
		assertEquals(BigDecimal.ZERO, bankAccount.getBalance());
		log.info("testUser email : {}",testUser.getEmail());
	}
}
