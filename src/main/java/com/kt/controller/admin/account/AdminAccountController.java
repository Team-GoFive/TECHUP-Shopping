package com.kt.controller.admin.account;

import java.util.UUID;

import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.AccountResponse;

import com.kt.domain.dto.response.PasswordRequestResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.service.admin.AdminAccountService;

import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController implements AdminAccountSwaggerSupporter {
	private final AdminAccountService AdminAccountService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAccounts(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	) {
		return page(
			AdminAccountService.searchAccounts(
				request,
				paging.toPageable()
			)
		);
	}

	@Override
	@DeleteMapping("/{accountId}")
	public ResponseEntity<ApiResult<Void>> deleteAccount(@PathVariable UUID accountId) {
		AdminAccountService.deleteAccount(accountId);
		return empty();
	}

	@Override
	@DeleteMapping("/{accountId}/force")
	public ResponseEntity<ApiResult<Void>> deleteAccountPermanently(@PathVariable UUID accountId) {
		AdminAccountService.deleteAccountPermanently(accountId);
		return empty();
	}

	@PatchMapping("/password-requests/{passwordRequestId}/reset")
	public ResponseEntity<ApiResult<Void>> resetAccountPassword(@PathVariable UUID passwordRequestId) {
		AdminAccountService.resetAccountPassword(passwordRequestId);
		return empty();
	}

	@PatchMapping("/password-requests/{passwordRequestId}/update")
	public ResponseEntity<ApiResult<Void>> updateAccountPassword(@PathVariable UUID passwordRequestId) {
		AdminAccountService.updateAccountPassword(passwordRequestId);
		return empty();
	}

	@Override
	@GetMapping("/password-requests")
	public ResponseEntity<ApiResult<PageResponse<PasswordRequestResponse.Search>>> searchPasswordRequests(
		@ParameterObject PasswordRequest.Search request,
		@ModelAttribute Paging paging) {
		return page(AdminAccountService.searchPasswordRequests(request, paging.toPageable()));
	}
}

