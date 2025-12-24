package com.kt.controller.refund;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Refund Query", description = "환불 조회 API")
public interface RefundQuerySwaggerSupporter {
	@Operation(
		summary = "내 환불 내역 조회",
		description = "회원이 본인의 환불 요청/처리 내역을 조회합니다."
	)
	@GetMapping("/refunds")
	ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getMyRefunds(
		@Parameter(hidden = true)
		DefaultCurrentUser currentUser,

		@Parameter(description = "페이징 정보")
		Paging paging
	);

	@Operation(
		summary = "셀러 환불 요청 목록 조회",
		description = "셀러가 자신의 상품에 대해 발생한 환불 내역을 조회합니다."
	)
	@GetMapping("/seller/refunds")
	ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getRequestedRefunds(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,

		@Parameter(description = "페이징 정보")
		Paging paging
	);
}
