package com.kt.controller.refund;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.kt.common.api.ApiResult.*;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.RefundHistoryRequest;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.refund.RefundQueryService;
import com.kt.service.refund.RefundService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/refunds")
public class RefundController implements RefundSwaggerSupporter {

	private final RefundService refundService;
	private final RefundQueryService refundQueryService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getMyRefunds(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		Paging paging
	) {
		return page(
			refundQueryService.getMyRefunds(currentUser.getId(), paging)
		);
	}

	@PostMapping
	public ResponseEntity<ApiResult<Void>> requestRefund(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid RefundHistoryRequest request
	) {
		refundService.requestRefund(
			currentUser.getId(),
			request.orderProductId(),
			request.reason()
		);

		return empty();
	}
}