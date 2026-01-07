package com.kt.service;

import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.response.PayResponse;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;

import com.kt.service.pay.PayService;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PayServiceTest {
	@Autowired
	UserRepository userRepository;

	@Autowired
	PayService payService;

	UserEntity testUser;

	static final long CHARGE_PAY_AMOUNT = 10_000;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		PayEntity pay = testUser.getPay();
		pay.charge(CHARGE_PAY_AMOUNT);
	}

	@Test
	@Transactional
	void 페이_잔액_조회_성공() {
		PayResponse.Balance response = payService.getBalance(testUser.getId());

		assertNotNull(response);
		assertEquals(response.balance(), BigDecimal.valueOf(CHARGE_PAY_AMOUNT));
		log.info("balance : {}", response.balance());

	}
}
