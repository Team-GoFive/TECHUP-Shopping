package com.kt.controller.review;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.dto.request.ReviewRequest;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "review", description = "리뷰 관련 API")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController extends SwaggerAssistance {
	private final ReviewService reviewService;

	@Operation(
		summary = "내가 작성한 리뷰 목록 조회",
		description = "내가 작성한 리뷰 목록을 조회하는 API"
	)
	@GetMapping("/mine")
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> searchMines(
		@ModelAttribute Paging paging,
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	) {
		return page(
			reviewService.getReviewsByUserId(
				paging.toPageable(),
				defaultCurrentUser.getId())
		);
	}

	@Operation(
		summary = "상품 리뷰 목록 조회",
		description = "특정 상품에 대한 리뷰 목록을 조회하는 API",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID")
		}
	)
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> search(
		@RequestParam UUID productId,
		@ModelAttribute Paging paging
	) {
		return page(
			reviewService.getReviewByProductId(
				productId,
				paging.toPageable()
			)
		);
	}

	@Operation(
		summary = "리뷰 수정",
		description = "리뷰 수정 API",
		parameters = {
			@Parameter(name = "reviewId", description = "리뷰 ID")
		}
	)
	@PatchMapping("/{reviewId}")
	public ResponseEntity<ApiResult<Void>> update(
		@Parameter(hidden = true) @AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID reviewId,
		@RequestBody ReviewRequest.Update request
	) {
		reviewService.update(
			currentUser.getEmail(),
			reviewId,
			request.content()
		);
		return empty();
	}

	@Operation(
		summary = "리뷰 삭제",
		description = "리뷰 삭제 API",
		parameters = {
			@Parameter(name = "reviewId", description = "리뷰 ID")
		}
	)
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@Parameter(hidden = true) @AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID reviewId
	) {
		reviewService.delete(currentUser.getEmail(), reviewId);
		return empty();
	}
}
