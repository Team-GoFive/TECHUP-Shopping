package com.kt.controller.admin.account;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerAssistanceInterface;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.response.AccountResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "account", description = "계정 관련 API")
public interface AdminAccountSwaggerSupporter extends SwaggerAssistanceInterface {

	@Operation(summary = "계정 검색", description = "회원, 배송기사를 검색")
	ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAccounts(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	);

	@Operation(summary = "계정 삭제 - soft", description = "회원과 배송기사를 soft delete 합니다.",
		parameters = {
			@Parameter(name = "accountId", description = "계정 Id")
		})
	@DeleteMapping("/accounts/{accountId}")
	ResponseEntity<ApiResult<Void>> deleteAccount(@PathVariable UUID accountId);

	@Operation(summary = "계정 삭제 - hard", description = "회원과 배송기사를 hard delete 합니다.", parameters = {
		@Parameter(name = "accountId", description = "계정 Id")
	})
	@DeleteMapping("/accounts/{accountId}/force")
	ResponseEntity<ApiResult<Void>> deleteAccountPermanently(@PathVariable UUID accountId);

}