package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class BankAccountEntityTest {

	@Test
	void 계좌_객체생성_성공() {
		UserEntity testUser = UserEntityCreator.createMember();
		BankAccountEntity bankAccount = testUser.getBankAccount();

		assertNotNull(bankAccount);
		assertEquals(0L, bankAccount.getBalance());
		assertEquals(bankAccount.getUser(), testUser);
	}
}
