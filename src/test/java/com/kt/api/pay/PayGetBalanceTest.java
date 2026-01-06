package com.kt.api.pay;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.PayRequest;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;

import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("주문 생성 - GET /api/pay/balance")
public class PayGetBalanceTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	UserEntity testUser;
	DefaultCurrentUser authenticator;

	static final long CHARGE_AMOUNT = 10_000;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		testUser.getPay().charge(CHARGE_AMOUNT);

		authenticator = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			AccountRole.MEMBER
		);
	}

	@Test
	void 페이_잔액_조회_성공() throws Exception {
		PayRequest.Charge request = new PayRequest.Charge(
			CHARGE_AMOUNT
		);

		mockMvc.perform(
				get("/api/pay/balance")
					.with(user(authenticator))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		log.info("pay balance : {}", testUser.getPay().getBalance());

	}
}
