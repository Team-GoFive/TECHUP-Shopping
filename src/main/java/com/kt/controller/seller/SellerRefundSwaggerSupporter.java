package com.kt.controller.seller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Refund", description = "판매자 환불 관련 API")
public interface SellerRefundSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "셀러 환불 요청 목록 조회",
		description = "판매자가 자신의 상품에 대해 발생한 환불 내역을 조회하는 API"
	)
	ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getRequestedRefunds(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,

		@Parameter(description = "페이징 정보")
		Paging paging
	);

	@Operation(
		summary = "환불 요청 승인",
		description = "판매자가 환불 요청을 승인하는 API",
		parameters = {
			@Parameter(name = "refundId", description = "환불 요청 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> approveRefund(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,
		UUID refundId
	);

	@Operation(
		summary = "환불 요청 거부",
		description = "판매자가 환불 요청을 거부하는 API",
		parameters = {
			@Parameter(name = "refundId", description = "환불 요청 ID")
		}
	)
	@PatchMapping
	ResponseEntity<ApiResult<Void>> rejectRefund(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,
		UUID refundId,
		RefundRejectRequest request
	);
}
