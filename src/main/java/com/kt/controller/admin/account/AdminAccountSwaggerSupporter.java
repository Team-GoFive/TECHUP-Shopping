package com.kt.controller.admin.account;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;

import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.PasswordRequest;
import com.kt.domain.dto.response.AccountResponse;

import com.kt.domain.dto.response.PasswordRequestResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Account", description = "관리자 계정 관리 관련 API")
public interface AdminAccountSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "계정 목록 조회",
		description = "관리자의 전체 계정 조회 API"
	)
	ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAccounts(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	);

	@Operation(
		summary = "계정 논리 삭제",
		description = "관리자의 계정 논리 삭제(Soft Delete) API",
		parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> deleteAccount(UUID accountId);


	@Operation(
		summary = "계정 물리 삭제",
		description = "관리자의 계정 물리 삭제(Hard Delete) API",
		parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> deleteAccountPermanently(UUID accountId);

	@Operation(
		summary = "계정 비밀번호 초기화",
		description = "관리자의 계정 비밀번호 초기화 API",
		parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> resetAccountPassword(UUID accountId);

	@Operation(
		summary = "계정 비밀번호 변경",
		description = "관리자의 계정 비밀번호 변경 API",
		parameters = {
			@Parameter(name = "accountId" , description = "계정 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> updateAccountPassword(UUID accountId);



	@Operation(
		summary = "계정 비밀번호 변경 및 초기화 요청 목록",
		description = "계정 비밀번호 변경 및 초기화 요청 목록 API"
	)
	ResponseEntity<ApiResult<PageResponse<PasswordRequestResponse.Search>>> searchPasswordRequests(
		@ParameterObject PasswordRequest.Search request,
		@ModelAttribute Paging paging
	);


}
