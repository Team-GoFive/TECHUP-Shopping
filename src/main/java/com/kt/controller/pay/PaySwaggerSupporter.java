package com.kt.controller.pay;

import com.kt.common.api.ApiResult;

import com.kt.domain.dto.request.PayRequest;

import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "Pay", description = "페이 관련 API")
public interface PaySwaggerSupporter {

	@Operation(
		summary = "페이 충전",
		description = "페이 잔액 충전API"
	)
	ResponseEntity<ApiResult<Void>> charge(
		DefaultCurrentUser currentUser,
		PayRequest.Charge request
	);
}
