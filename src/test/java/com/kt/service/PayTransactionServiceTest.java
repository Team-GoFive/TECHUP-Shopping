package com.kt.service;

import com.kt.common.UserEntityCreator;
import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.dto.request.PayTransactionRequest;
import com.kt.domain.dto.response.PayTransactionResponse;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.PayTransactionEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.pay.transaction.PayTransactionRepository;
import com.kt.repository.user.UserRepository;

import com.kt.service.pay.transaction.PayTransactionService;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PayTransactionServiceTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PayTransactionService payTransactionService;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Autowired
	PayTransactionRepository payTransactionRepository;

	UserEntity testUser;

	static final long CHARGE_PAY_AMOUNT = 1_000_000;


	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		testUser.getPay().charge(CHARGE_PAY_AMOUNT);

		savePayTransaction();
	}

	@Test
	@Transactional
	void 페이_거래내역_조회_성공() {
		PayTransactionRequest.Search request = new PayTransactionRequest.Search(
			PayTransactionType.CREDIT,
			null,
			null,
			null
		);
		Pageable pageable = PageRequest.of(0, 10);

		Page<PayTransactionResponse.Search> response =
			payTransactionService.getTransactions(
				testUser.getId(),
				request,
				pageable
			);

		Assertions.assertNotNull(response);
		log.info("result getFist : {}", response.get().findFirst());
		log.info("result size : {}", response.getTotalElements());
		assertEquals(1, response.getTotalElements());
	}

	private void savePayTransaction() {

		BankAccountEntity bankAccount = BankAccountEntity.create(
			testUser, testUser.getName()
		);
		bankAccountRepository.save(bankAccount);

		PayTransactionEntity payTransaction = PayTransactionEntity.create(
			testUser.getPay(),
			PayTransactionType.CREDIT,
			PayTransactionPurpose.CHARGE,
			CHARGE_PAY_AMOUNT,
			testUser.getPay().getBalance(),
			testUser.getPay().getDisplayName(),
			bankAccount.getDisplayName()
		);
		payTransactionRepository.save(payTransaction);
	}


}
