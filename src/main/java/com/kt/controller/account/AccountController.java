package com.kt.controller.account;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountSwaggerSupporter {
	private final AccountService accountService;

	@Override
	@PatchMapping("/{accountId}/password")
	public ResponseEntity<ApiResult<Void>> updatePassword(
		@PathVariable UUID accountId,
		@RequestBody @Valid AccountRequest.UpdatePassword request
	) {
		accountService.updatePassword(
			accountId,
			request.currentPassword(),
			request.newPassword()
		);
		return empty();
	}

	@Override
	@DeleteMapping("/retire")
	public ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	) {
		UUID accountId = defaultCurrentUser.getId();
		accountService.deleteAccount(accountId);
		return empty();
	}
}
