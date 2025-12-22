package com.kt.controller.refund;

import com.kt.common.support.SwaggerSupporter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.RefundHistoryRequest;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Refund", description = "사용자/판매자 환불 관련 API")
public interface RefundSwaggerSupporter extends SwaggerSupporter {
	@Operation(
		summary = "환불 요청",
		description = "사용자가 주문 상품에 대해 환불을 요청하는 API"
	)
	ResponseEntity<ApiResult<Void>> requestRefund(
		@Parameter(hidden = true)
		DefaultCurrentUser currentUser,
		RefundHistoryRequest request
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
	ResponseEntity<ApiResult<Void>> rejectRefund(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,
		UUID refundId,
		RefundRejectRequest request
	);
}
