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
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "account", description = "계정 관련 API")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController extends SwaggerAssistance {
	private final AccountService accountService;

	@Operation(
		summary = "비밀번호 변경",
		description = "계정의 비밀번호 변경 관련 API"
		, parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
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

	@Operation(
		summary = "계정 탈퇴",
		description = "로그인한 계정 탈퇴 관련 API"
	)
	@DeleteMapping("/retire")
	public ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	) {
		UUID accountId = defaultCurrentUser.getId();
		accountService.deleteAccount(accountId);
		return empty();
	}
}
