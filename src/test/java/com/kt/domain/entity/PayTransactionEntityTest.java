package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class PayTransactionEntityTest {

	UserEntity testUser;
	static final long AMOUNT = 20_000;
	@BeforeEach
	void init() {
		testUser = UserEntityCreator.create();
		testUser.getBankAccount().deposit(1_000_000);
	}

	@Test
	void 객체생성_성공() {
		PayEntity pay = testUser.getPay();
		pay.charge(AMOUNT);
		PayTransactionEntity payTransaction = PayTransactionEntity.create(
			pay,
			PayTransactionType.CREDIT,
			PayTransactionPurpose.CHARGE,
			AMOUNT,
			pay.getBalance(),
			testUser.getBankAccount().getId()
		);
		assertNotNull(payTransaction);
		assertEquals(BigDecimal.valueOf(AMOUNT), payTransaction.getBalanceSnapshot());
	}
}
