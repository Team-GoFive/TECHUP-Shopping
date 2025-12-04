package com.kt.controller.admin;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "admin", description = "관리자 관리 관련 API")
public interface AdminSwaggerSupporter {
	@Operation(
		summary = "관리자 검색", description = "관리자를 검색"
	)
	ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAdmins(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	);

	@Operation(
		summary = "관리자 상세 조회", description = "관리자 상세 조회",
		parameters = {
			@Parameter(name = "adminId", description = "관리자 Id")
		}
	)
	ResponseEntity<ApiResult<UserResponse.UserDetail>> getAdminDetail(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID adminId
	);

	@Operation(
		summary = "관리자 생성", description = "관리자를 생성합니다."
	)
	ResponseEntity<ApiResult<Void>> createAdmin(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid SignupRequest.SignupMember request
	);

	@Operation(
		summary = "관리자 정보 수정", description = "관리자 정보를 수정합니다."
	)
	ResponseEntity<ApiResult<Void>> updateAdmin(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid UserRequest.UpdateDetails request,
		@PathVariable UUID adminId
	);

	@Operation(
		summary = "관리자 hard delete", description = "관리자를 삭제합니다."
	)
	ResponseEntity<ApiResult<Void>> deleteAdmin(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID adminId
	);
}
