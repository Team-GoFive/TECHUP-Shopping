package com.kt.service;

import com.kt.common.UserEntityCreator;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;

import com.kt.repository.BankAccountTransactionRepository;
import com.kt.repository.PayTransactionRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.pay.PayChargeService;

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
public class PayChargeServiceTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PayChargeService payChargeService;

	@Autowired
	BankAccountTransactionRepository bankAccountTransactionRepository;

	@Autowired
	PayTransactionRepository payTransactionRepository;

	UserEntity testUser;
	static final long DEPOSIT_BANK_ACCOUNT_AMOUNT = 1_000_000;
	static final long CHARGE_PAY_AMOUNT = 10_000;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		testUser.getBankAccount().deposit(DEPOSIT_BANK_ACCOUNT_AMOUNT);
	}

	@Test
	@Transactional
	void 유저_페이충전_성공() {
		PayEntity pay = testUser.getPay();
		BankAccountEntity bankAccount = testUser.getBankAccount();
		payChargeService.charge(CHARGE_PAY_AMOUNT, testUser.getId());
		BigDecimal bankAccountBalance = BigDecimal.valueOf(DEPOSIT_BANK_ACCOUNT_AMOUNT - CHARGE_PAY_AMOUNT);
		BigDecimal payBalance = BigDecimal.valueOf(CHARGE_PAY_AMOUNT);
		assertEquals(payBalance, pay.getBalance());
		assertEquals(bankAccountBalance, bankAccount.getBalance());

		assertEquals(1, payTransactionRepository.findAll().size());
		assertEquals(1, bankAccountTransactionRepository.findAll().size());

		log.info("pay balance :: {}", pay.getBalance());
		log.info("bankAccount balance :: {}", bankAccount.getBalance());
	}
}
