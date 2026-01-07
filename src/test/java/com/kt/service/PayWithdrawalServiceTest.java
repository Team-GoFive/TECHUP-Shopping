package com.kt.service;

import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.pay.transaction.PayTransactionRepository;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;
import com.kt.repository.user.UserRepository;

import com.kt.service.pay.PayWithdrawalService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PayWithdrawalServiceTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PayWithdrawalService payWithdrawalService;

	@Autowired
	BankAccountTransactionRepository bankAccountTransactionRepository;

	@Autowired
	PayTransactionRepository payTransactionRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	UserEntity testUser;
	BankAccountEntity bankAccount;

	static final long DEPOSIT_BANK_ACCOUNT_AMOUNT = 1_000_000;
	static final long CHARGE_PAY_AMOUNT = 10_000;
	static final long WITHDRAW_PAY_AMOUNT = 5_000;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		bankAccount = BankAccountEntity.create(testUser, testUser.getName());
		bankAccountRepository.save(bankAccount);
		bankAccount.deposit(DEPOSIT_BANK_ACCOUNT_AMOUNT);
		bankAccount.withdraw(CHARGE_PAY_AMOUNT);

		testUser.getPay().charge(CHARGE_PAY_AMOUNT);
	}

	@Test
	@Transactional
	void 유저_페이인출_성공() {
		PayEntity pay = testUser.getPay();
		payWithdrawalService.withdraw(WITHDRAW_PAY_AMOUNT, testUser.getId());
		BigDecimal payBalance = BigDecimal.valueOf(CHARGE_PAY_AMOUNT - WITHDRAW_PAY_AMOUNT);
		BigDecimal bankAccountBalance = BigDecimal.valueOf(
			DEPOSIT_BANK_ACCOUNT_AMOUNT - CHARGE_PAY_AMOUNT + WITHDRAW_PAY_AMOUNT
		);
		assertEquals(payBalance, pay.getBalance());
		assertEquals(bankAccountBalance, bankAccount.getBalance());
		assertEquals(1, payTransactionRepository.findAll().size());
		assertEquals(1, bankAccountTransactionRepository.findAll().size());
		log.info("pay balance :: {}", pay.getBalance());
		log.info("bankAccount balance :: {}", bankAccount.getBalance());
	}
}
