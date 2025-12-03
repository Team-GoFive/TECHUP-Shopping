package com.kt.controller.account;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Account", description = "계정 관련 API")
public interface AccountSwaggerSupporter extends SwaggerSupporter {
	@Operation(
		summary = "비밀번호 변경",
		description = "계정의 비밀번호 변경 관련 API",
		parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> updatePassword(
		UUID accountId,
		AccountRequest.UpdatePassword request
	);

	@Operation(
		summary = "계정 탈퇴",
		description = "로그인한 계정 탈퇴 관련 API"
	)
	ResponseEntity<ApiResult<Void>> delete(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	);
}
