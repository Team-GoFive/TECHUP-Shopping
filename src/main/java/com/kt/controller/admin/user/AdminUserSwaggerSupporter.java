package com.kt.controller.admin.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "회원 관련 API")
public interface AdminUserSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "회원 상세 조회", parameters = {
		@Parameter(name = "회원 Id")
	}
	)
	ResponseEntity<ApiResult<UserResponse.UserDetail>> getUserDetail(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	);

	@Operation(
		summary = "회원 활성화", parameters = {
		@Parameter(name = "회원 Id")
	}
	)
	ResponseEntity<ApiResult<Void>> enableAccount(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	);

	@Operation(
		summary = "회원 비활성화", parameters = {
		@Parameter(name = "회원 Id")
	}
	)
	ResponseEntity<ApiResult<Void>> disableAccount(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	);

	@Operation(
		summary = "회원 삭제", parameters = {
		@Parameter(name = "회원 Id")
	}
	)
	ResponseEntity<ApiResult<Void>> deleteAccount(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	);
}

