package com.kt.api.bankaccount.transaction;

import com.kt.common.MockMvcTest;

import com.kt.common.UserEntityCreator;
import com.kt.constant.SortDirection;
import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.entity.BankAccountEntity;
import com.kt.domain.entity.BankAccountTransactionEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.bankaccount.BankAccountRepository;
import com.kt.repository.bankaccount.transaction.BankAccountTransactionRepository;
import com.kt.repository.user.UserRepository;

import com.kt.security.DefaultCurrentUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 조회 - GET /api/bank-account-transactions")
public class BankAccountTransactionSearchTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BankAccountRepository bankAccountRepository;

	@Autowired
	BankAccountTransactionRepository bankAccountTransactionRepository;

	UserEntity testUser;
	DefaultCurrentUser userDetails;

	@BeforeEach
	void setup() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);
		BankAccountEntity bankAccount = BankAccountEntity.create(testUser, testUser.getName());
		bankAccountRepository.save(bankAccount);

		userDetails = new DefaultCurrentUser(
			testUser.getId(),
			testUser.getEmail(),
			testUser.getRole()
		);

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
	void 계좌_거래내역_조회_성공() throws Exception {

		BankAccountTransactionRequest.Search request = new BankAccountTransactionRequest.Search(
			null,
			null,
			null,
			SortDirection.ASC,
			null
		);
		String requestToJson = objectMapper.writeValueAsString(request);
		mockMvc.perform(
			get("/api/bank-account-transactions")
				.param("page", "1")
				.param("size", "10")
				.param("type", "")
				.param("fromDate", "")
				.param("toDate", "")
				.param("sort", SortDirection.ASC.name())
				.param("keyword", "")
				.with(user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestToJson)
		)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.totalCount").value(1),
				jsonPath("$.data.totalPages").value(1)
			);


	}

}
