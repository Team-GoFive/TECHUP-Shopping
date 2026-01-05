package com.kt.api.pay;

import com.kt.common.MockMvcTest;
import com.kt.common.UserEntityCreator;
import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.PayRequest;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.user.UserRepository;

import com.kt.security.DefaultCurrentUser;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ActiveProfiles("test")
@DisplayName("주문 생성 - POST /api/pay/withdrawals")
public class PayWithdrawalTest extends MockMvcTest {
	@Autowired
	UserRepository userRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	UserEntity testUser;
	DefaultCurrentUser authenticator;
	BankAccountEntity bankAccount;

	static final long DEPOSIT_AMOUNT = 1_000_000;
	static final long CHARGE_AMOUNT = 10_000;
	static final long WITHDRAWAL_AMOUNT = 5_000;
	static final String DISPLAY_NAME_SUFFIX = "_계좌";
	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		String bankAccountDisplayName = testUser.getName() + DISPLAY_NAME_SUFFIX;
		bankAccount = BankAccountEntity.create(testUser, bankAccountDisplayName);
		bankAccountRepository.save(bankAccount);

		testUser.getPay().charge(CHARGE_AMOUNT);
		bankAccount.deposit(DEPOSIT_AMOUNT);
		bankAccount.withdraw(CHARGE_AMOUNT);

		authenticator = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			AccountRole.MEMBER
		);
	}

	@Test
	@Transactional
	void 페이_인출_성공() throws Exception {
		PayRequest.Withdrawal request = new PayRequest.Withdrawal(
			WITHDRAWAL_AMOUNT
		);

		mockMvc.perform(
				post("/api/pay/withdrawals")
					.with(user(authenticator))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			).andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		BigDecimal payBalance = BigDecimal.valueOf(CHARGE_AMOUNT - WITHDRAWAL_AMOUNT);
		BigDecimal bankAccountBalance = BigDecimal.valueOf((DEPOSIT_AMOUNT - CHARGE_AMOUNT) + WITHDRAWAL_AMOUNT);
		assertEquals(payBalance, testUser.getPay().getBalance());
		assertEquals(bankAccountBalance, bankAccount.getBalance());
		log.info("pay balance : {}", testUser.getPay().getBalance());
		log.info("bankAccount balance : {}", bankAccount.getBalance());

	}
}
