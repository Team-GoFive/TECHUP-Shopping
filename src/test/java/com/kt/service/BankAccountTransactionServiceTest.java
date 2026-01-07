package com.kt.service;

import com.kt.common.UserEntityCreator;
import com.kt.constant.SortDirection;
import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.dto.response.BankAccountTransactionResponse;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.BankAccountTransactionEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.bankaccount.transaction.BankAccountTransactionServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class BankAccountTransactionServiceTest {

	@Autowired
	BankAccountTransactionServiceImpl bankAccountTransactionService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Autowired
	BankAccountTransactionRepository bankAccountTransactionRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	UserEntity testMember;

	String testEmail = "test@test.com";
	String rawPassword = "1234";

	@BeforeEach
	void setup() {
		testMember = UserEntityCreator.create(
			testEmail, passwordEncoder.encode(rawPassword)
		);
		userRepository.save(testMember);
		BankAccountEntity bankAccount = BankAccountEntity.create(
			testMember,
			testMember.getName()
		);

		bankAccountRepository.save(bankAccount);

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
		bankAccountTransactionRepository.save(bankAccountTransaction);
	}

	@Test
	@Transactional
	void 계좌_거래내역_조회_성공() {
		BankAccountTransactionRequest.Search request = new BankAccountTransactionRequest.Search(
			null,
			null,
			null,
			SortDirection.ASC,
			null
		);
		Pageable pageable = PageRequest.of(0, 10);
		Page<BankAccountTransactionResponse.Search> result =
			bankAccountTransactionService.getTransactions(
				testMember.getId(),
				request,
				pageable
			);

		assertNotNull(result);
		log.info("result getFist : {}", result.get().findFirst());
		log.info("result size : {}", result.getTotalElements());

		assertEquals(1, result.getTotalElements());


	}

}
