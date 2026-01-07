package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;

import com.kt.exception.FieldValidationException;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
public class PayTransactionEntityTest {

	UserEntity testUser;
	BankAccountEntity bankAccount;

	static final long AMOUNT = 20_000;

	@BeforeEach
	void init() {
		testUser = UserEntityCreator.create();
		bankAccount = BankAccountEntity.create(testUser, testUser.getName());
		bankAccount.deposit(1_000_000);
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
			pay.getDisplayName(),
			bankAccount.getDisplayName()
		);
		assertNotNull(payTransaction);
		assertEquals(BigDecimal.valueOf(AMOUNT), payTransaction.getBalanceSnapshot());
	}

	@Test
	void 객체생성_실패_페이_거래타입_null() {
		PayEntity pay = testUser.getPay();
		assertThatThrownBy(() ->
			PayTransactionEntity.create(
				testUser.getPay(),
				null,
				PayTransactionPurpose.CHARGE,
				10_000,
				BigDecimal.valueOf(10_000),
				bankAccount.getDisplayName(),
				pay.getDisplayName()
			)
		).isInstanceOfSatisfying(FieldValidationException.class, ex -> {
			log.info("getErrorMessage : {}", ex.getErrorMessage());
			assertEquals("도메인 필드 오류 : 페이 거래타입은(는) 필수 항목입니다.", ex.getErrorMessage());
		});
	}

	@Test
	void 객체생성_실패_페이_거래목적_null() {
		PayEntity pay = testUser.getPay();
		assertThatThrownBy(() ->
			PayTransactionEntity.create(
				pay,
				PayTransactionType.CREDIT,
				null,
				10_000,
				BigDecimal.valueOf(10_000),
				bankAccount.getDisplayName(),
				pay.getDisplayName()
			)
		).isInstanceOfSatisfying(FieldValidationException.class, ex -> {
			log.info("getErrorMessage : {}", ex.getErrorMessage());
			assertEquals("도메인 필드 오류 : 페이 거래목적은(는) 필수 항목입니다.", ex.getErrorMessage());
		});
	}

	@Test
	void 객체생성_실패_페이_거래금액_0_이하() {
		PayEntity pay = testUser.getPay();
		assertThatThrownBy(() ->
			PayTransactionEntity.create(
				pay,
				PayTransactionType.CREDIT,
				PayTransactionPurpose.CHARGE,
				0,
				BigDecimal.valueOf(10_000),
				bankAccount.getDisplayName(),
				pay.getDisplayName()
			)
		)
			.isInstanceOfSatisfying(FieldValidationException.class, ex -> {
				log.info("getErrorMessage : {}", ex.getErrorMessage());
				assertEquals("도메인 필드 오류 : 페이 거래금액은(는) 0보다 커야합니다.", ex.getErrorMessage());
			});
	}

	@Test
	void 객체생성_실패_페이_거래후_잔액_0_미만() {
		PayEntity pay = testUser.getPay();
		assertThatThrownBy(() ->
			PayTransactionEntity.create(
				pay,
				PayTransactionType.DEBIT,
				PayTransactionPurpose.WITHDRAW,
				10_000,
				BigDecimal.valueOf(-1),
				pay.getDisplayName(),
				bankAccount.getDisplayName()
			)
		).isInstanceOfSatisfying(
			FieldValidationException.class, ex -> {
				log.info("getErrorMessage : {}", ex.getErrorMessage());
				assertEquals("도메인 필드 오류 : 거래 후 잔액은(는) 0보다 작을 수 없습니다.", ex.getErrorMessage());
			}
		);
	}

}
