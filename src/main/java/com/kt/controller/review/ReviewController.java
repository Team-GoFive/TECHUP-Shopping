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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.ReviewRequest;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.review.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewSwaggerSupporter {
	private final ReviewService reviewService;

	@PostMapping
	@Override
	public ResponseEntity<ApiResult<Void>> create(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody ReviewRequest.Create request
	) {
		reviewService.create(
			currentUser.getId(),
			request.orderProductId(),
			request.content()
		);
		return empty();
	}

	@GetMapping("/mine")
	@Override
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> searchMines(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@Valid @ModelAttribute Paging paging
	) {
		return page(
			reviewService.getReviewsByUserId(
				paging.toPageable(),
				defaultCurrentUser.getId())
		);
	}

	@GetMapping
	@Override
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> search(
		@RequestParam UUID productId,
		@Valid @ModelAttribute Paging paging
	) {
		return page(
			reviewService.getReviewByProductId(
				productId,
				paging.toPageable()
			)
		);
	}

	@PatchMapping("/{reviewId}")
	@Override
	public ResponseEntity<ApiResult<Void>> update(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID reviewId,
		@RequestBody ReviewRequest.Update request
	) {
		reviewService.update(
			currentUser.getId(),
			reviewId,
			request.content()
		);
		return empty();
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID reviewId
	) {
		reviewService.delete(currentUser.getId(), reviewId);
		return empty();
	}
}
