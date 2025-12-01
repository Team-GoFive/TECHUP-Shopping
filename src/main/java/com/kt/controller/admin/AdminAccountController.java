package com.kt.controller.admin;

import java.util.UUID;

import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.response.AccountResponse;

import com.kt.domain.dto.response.UserResponse;

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
import com.kt.service.UserService;

import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAccountController {
	private final UserService userService;
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

	@GetMapping("/users/{userId}")
	public ResponseEntity<ApiResult<UserResponse.UserDetail>> getAccountDetail(@PathVariable UUID userId) {
		return wrap(userService.getUserDetail(userId));
	}

	@PatchMapping("/users/{userId}/enabled")
	public ResponseEntity<ApiResult<Void>> enableAccount(@PathVariable UUID userId) {
		userService.enableUser(userId);
		return empty();
	}

	@PatchMapping("/users/{userId}/disabled")
	public ResponseEntity<ApiResult<Void>>  disableAccount(@PathVariable UUID userId) {
		userService.disableUser(userId);
		return empty();
	}

	@PatchMapping("/users/{userId}/removed")
	public ResponseEntity<ApiResult<Void>> deleteAccount(@PathVariable UUID userId) {
		userService.deleteUser(userId);
		return empty();
	}

}


