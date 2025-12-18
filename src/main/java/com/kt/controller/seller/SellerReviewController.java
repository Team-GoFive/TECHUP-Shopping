package com.kt.controller.seller;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.response.SellerReviewResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.seller.SellerReviewService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/reviews")
public class SellerReviewController {
	private final SellerReviewService sellerReviewService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<SellerReviewResponse.Search>>> getAllReviews(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@Valid @ParameterObject Paging paging
	) {
		return page(
			sellerReviewService.getAllReviews(
				paging.toPageable(), defaultCurrentUser.getId()
			)
		);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResult<PageResponse<SellerReviewResponse.Search>>> getReviewsByProduct(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@Valid @ParameterObject Paging paging,
		@PathVariable UUID productId
	) {
		return page(
			sellerReviewService.getReviewsByProduct(
				paging.toPageable(),
				defaultCurrentUser.getId(),
				productId
			)
		);
	}

}
