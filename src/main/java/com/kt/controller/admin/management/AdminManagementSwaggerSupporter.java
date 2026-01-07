package com.kt.controller.admin.management;

import java.util.UUID;

import com.kt.domain.dto.request.AdminRequest;

import com.kt.domain.dto.response.AdminResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
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

@Tag(name = "Admin", description = "관리자 관리 관련 API")
public interface AdminManagementSwaggerSupporter {

	@Operation(
		summary = "관리자 상세 조회", description = "관리자 상세 조회"
	)
	ResponseEntity<ApiResult<AdminResponse.Detail>> me();

	@Operation(
		summary = "관리자 정보 수정", description = "관리자 정보를 수정합니다."
	)
	ResponseEntity<ApiResult<Void>> update(
		@RequestBody @Valid AdminRequest.Update request
	);
}
