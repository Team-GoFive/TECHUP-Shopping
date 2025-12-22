package com.kt.controller.refund;


import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.RefundHistoryRequest;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.RefundService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefundController implements RefundSwaggerSupporter {

	private final RefundService refundService;

	@PostMapping("/refunds")
	public ResponseEntity<ApiResult<Void>> requestRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid RefundHistoryRequest request
	) {
		refundService.requestRefund(
			currentUser.getId(),
			request.orderProductId(),
			request.reason()
		);

		return ApiResult.empty();
	}

	@PatchMapping("/seller/refunds/{refundId}/approve")
	public ResponseEntity<ApiResult<Void>> approveRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		@PathVariable UUID refundId
	) {
		refundService.approveRefund(
			currentSeller.getId(),
			refundId
		);

		return ApiResult.empty();
	}

	@PatchMapping("/seller/refunds/{refundId}/reject")
	public ResponseEntity<ApiResult<Void>> rejectRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		@PathVariable UUID refundId,
		@RequestBody @Valid RefundRejectRequest request
	) {
		refundService.rejectRefund(
			currentSeller.getId(),
			refundId,
			request.reason()
		);

		return ApiResult.empty();
	}
}