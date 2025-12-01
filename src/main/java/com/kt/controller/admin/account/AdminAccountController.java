package com.kt.controller.admin.account;

import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.response.AccountResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.service.AccountService;

import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

// TODO: 계정 soft delete
// TODO: 계정 hard delete

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

}


