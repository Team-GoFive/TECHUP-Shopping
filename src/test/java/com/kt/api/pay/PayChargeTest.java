package com.kt.api.pay;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.PayRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;

import com.kt.security.DefaultCurrentUser;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@Slf4j
@ActiveProfiles("test")
@DisplayName("주문 생성 - POST /api/pay/charges")
public class PayChargeTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	UserEntity testUser;
	DefaultCurrentUser authenticator;
	static final long DEPOSIT_AMOUNT = 1_000_000;
	static final long CHARGE_AMOUNT = 10_000;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		testUser.getBankAccount().deposit(DEPOSIT_AMOUNT);
		userRepository.save(testUser);
		log.info("testUser.getBankAccount() :: {}", testUser.getBankAccount().getBalance());
		authenticator = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			AccountRole.MEMBER
		);
	}

	@Test
	@Transactional
	void 페이_충전_성공() throws Exception {
		PayRequest.Charge request = new PayRequest.Charge(
			CHARGE_AMOUNT
		);

		mockMvc.perform(
			post("/api/pay/charges")
				.with(user(authenticator))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		log.info("pay balance : {}", testUser.getPay().getBalance());


	}
}
