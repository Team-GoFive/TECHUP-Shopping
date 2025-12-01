package com.kt.controller.admin.account;


import com.kt.domain.dto.response.AccountResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.service.AccountService;

import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAccountController {
	private final AccountService accountService;

	@GetMapping("/accounts")
	public ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAccounts(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	) {
		return page(
			accountService.searchAccounts(
				request,
				paging.toPageable()
			)
		);
	}

	@PatchMapping("/accounts/{accountId}")
	public ResponseEntity<ApiResult<Void>> deleteAccount(@PathVariable UUID accountId) {
		accountService.deleteAccount(accountId);
		return empty();
	}

	@DeleteMapping("/accounts/{accountId}/force")
	public ResponseEntity<ApiResult<Void>> deleteAccountPermanently(@PathVariable UUID accountId) {
		accountService.deleteAccountPermanently(accountId);
		return empty();
	}

}
