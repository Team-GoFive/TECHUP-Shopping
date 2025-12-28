package com.kt.controller.refund;

import com.kt.common.Paging;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.RefundHistoryRequest;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Refund", description = "사용자 환불 관련 API")
public interface RefundSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "내 환불 내역 조회",
		description = "회원이 본인의 환불 요청/처리 내역을 조회하는 API"
	)
	@GetMapping
	ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getMyRefunds(
		@Parameter(hidden = true)
		DefaultCurrentUser currentUser,

		@Parameter(description = "페이징 정보")
		Paging paging
	);

	@Operation(
		summary = "환불 요청",
		description = "사용자가 주문 상품에 대해 환불을 요청하는 API"
	)
	@PostMapping
	ResponseEntity<ApiResult<Void>> requestRefund(
		@Parameter(hidden = true)
		DefaultCurrentUser currentUser,
		RefundHistoryRequest request
	);
}
