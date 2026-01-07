package com.kt.domain.entity;

import com.kt.common.UserEntityCreator;

import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;

import com.kt.exception.FieldValidationException;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
public class BankAccountTransactionEntityTest {

	UserEntity testUser;
	BankAccountEntity bankAccount;

	@BeforeEach
	void init() {
		testUser = UserEntityCreator.create();
		bankAccount = BankAccountEntity.create(testUser, testUser.getName());
	}

	@Test
	void 계좌거래내역_객체생성_성공() {
		BankAccountTransactionEntity bankAccountTransaction =
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				BankAccountTransactionPurpose.SALARY,
				10_000,
				BigDecimal.valueOf(10_000),
				"급여입금",
				bankAccount.getDisplayName()
			);
		assertNotNull(bankAccountTransaction);
	}

	@Test
	void 객체생성_실패_계좌_거래타입_null() {

		assertThatThrownBy(() ->
			BankAccountTransactionEntity.create(
				bankAccount,
				null,
				BankAccountTransactionPurpose.SALARY,
				10_000,
				BigDecimal.valueOf(10_000),
				"급여입금",
				bankAccount.getDisplayName()
			)
		)
			.isInstanceOfSatisfying(FieldValidationException.class, ex -> {
				log.info("getErrorMessage : {}", ex.getErrorMessage());
				assertEquals("도메인 필드 오류 : 거래 타입은(는) 필수 항목입니다.", ex.getErrorMessage());
			});
	}

	@Test
	void 객체생성_실패_계좌_거래목적_null() {

		assertThatThrownBy(() ->
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				null,
				10_000,
				BigDecimal.valueOf(10_000),
				"급여입금",
				bankAccount.getDisplayName()
			)
		).isInstanceOfSatisfying(FieldValidationException.class, ex -> {
				log.info("getErrorMessage : {}", ex.getErrorMessage());
				assertEquals("도메인 필드 오류 : 거래 목적은(는) 필수 항목입니다.", ex.getErrorMessage());
			});
	}

	@Test
	void 객체생성_실패_계좌_거래금액_0_이하() {

		assertThatThrownBy(() ->
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				BankAccountTransactionPurpose.SALARY,
				0,
				BigDecimal.valueOf(10_000),
				"급여입금",
				bankAccount.getDisplayName()
			)
		)
			.isInstanceOfSatisfying(FieldValidationException.class, ex -> {
				log.info("getErrorMessage : {}", ex.getErrorMessage());
				assertEquals("도메인 필드 오류 : 거래 금액은(는) 0보다 커야합니다.", ex.getErrorMessage());
			});
	}

	@Test
	void 객체생성_실패_계좌_거래후_잔액_0_미만() {

		assertThatThrownBy(() ->
			BankAccountTransactionEntity.create(
				bankAccount,
				BankAccountTransactionType.DEPOSIT,
				BankAccountTransactionPurpose.SALARY,
				10_000,
				BigDecimal.valueOf(-1),
				"급여입금",
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


