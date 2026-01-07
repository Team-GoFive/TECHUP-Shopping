package com.kt.controller.seller.refund;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.refund.RefundQueryService;
import com.kt.service.seller.refund.SellerRefundService;

import static com.kt.common.api.ApiResult.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seller/refunds")
@RequiredArgsConstructor
public class SellerRefundController implements SellerRefundSwaggerSupporter {

	private final SellerRefundService sellerRefundService;
	private final RefundQueryService refundQueryService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getRequestedRefunds(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		Paging paging
	) {
		return page(
			refundQueryService.getRequestedRefunds(currentSeller.getId(), paging)
		);
	}

	@PatchMapping("/{refundId}/approve")
	public ResponseEntity<ApiResult<Void>> approveRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		@PathVariable UUID refundId
	) {
		sellerRefundService.approveRefund(
			currentSeller.getId(),
			refundId
		);

		return empty();
	}

	@PatchMapping("/{refundId}/reject")
	public ResponseEntity<ApiResult<Void>> rejectRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		@PathVariable UUID refundId,
		@RequestBody @Valid RefundRejectRequest request
	) {
		sellerRefundService.rejectRefund(
			currentSeller.getId(),
			refundId,
			request.reason()
		);

		return empty();
	}
}
