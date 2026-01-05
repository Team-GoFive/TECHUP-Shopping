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

	static final String DISPLAY_NAME_SUFFIX = "_계좌";

	@Test
	void 계좌_객체생성_성공() {
		UserEntity testUser = UserEntityCreator.create();
		String bankAccountDisplayName = testUser.getName() + DISPLAY_NAME_SUFFIX;
		BankAccountEntity bankAccount = BankAccountEntity.create(testUser, bankAccountDisplayName);

		assertNotNull(bankAccount);
		assertEquals(BigDecimal.ZERO, bankAccount.getBalance());
		log.info("testUser email : {}",testUser.getEmail());
	}
}
