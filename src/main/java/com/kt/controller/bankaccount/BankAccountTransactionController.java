package com.kt.controller.bankaccount;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.dto.response.BankAccountTransactionResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.bankaccount.transaction.BankAccountTransactionService;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.kt.common.api.ApiResult.page;

@RestController
@RequestMapping("/api/bank-account-transactions")
@RequiredArgsConstructor
public class BankAccountTransactionController {

	private final BankAccountTransactionService bankAccountTransactionService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<BankAccountTransactionResponse.Search>>> getMyTransactions(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@ParameterObject BankAccountTransactionRequest.Search request,
		@ModelAttribute Paging paging
	) {

		UUID holderId = currentUser.getId();

		return page(
			bankAccountTransactionService.getTransactions(
				holderId,
				request,
				paging.toPageable()
			)
		);
	}

}
