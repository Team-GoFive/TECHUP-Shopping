package com.kt.controller.admin.review;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface AdminReviewSwaggerSupporter {

	@Operation(
		summary = "리뷰 조회", description = "카테고리, 상품명으로 리뷰 검색이 가능합니다."
	)
	ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> search(
		@ModelAttribute Paging paging,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type
	);

	@Operation(
		summary = "리뷰 삭제", description = "관리자가 리뷰를 삭제합니다."
	)
	ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID reviewId
	);
}
