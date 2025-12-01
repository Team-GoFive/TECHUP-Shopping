package com.kt.controller.admin.review;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
	private final ReviewService reviewService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> search(
		@ModelAttribute Paging paging,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type
	) {
		return page(
			reviewService.getReviewsByAdmin(
				paging.toPageable(),
				keyword,
				type
			)
		);
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID reviewId
	) {
		reviewService.delete(reviewId);
		return empty();
	}
}
