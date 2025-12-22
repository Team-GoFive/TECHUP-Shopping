package com.kt.controller.refund;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.RefundQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefundQueryController {

	private final RefundQueryService refundQueryService;

	@GetMapping("/refunds")
	public ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getMyRefunds(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		Paging paging
	) {
		return ApiResult.page(
			refundQueryService.getMyRefunds(currentUser.getId(), paging)
		);
	}

	@GetMapping("/seller/refunds")
	public ResponseEntity<ApiResult<PageResponse<RefundQueryResponse>>> getRequestedRefunds(
		@AuthenticationPrincipal DefaultCurrentUser currentSeller,
		Paging paging
	) {
		return ApiResult.page(
			refundQueryService.getRequestedRefunds(currentSeller.getId(), paging)
		);
	}
}
