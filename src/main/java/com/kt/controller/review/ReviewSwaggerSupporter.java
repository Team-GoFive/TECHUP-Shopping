package com.kt.controller.review;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.ReviewRequest;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "review", description = "리뷰 관련 API")
public interface ReviewSwaggerSupporter {

	@Operation(
		summary = "리뷰 작성", description = "리뷰 작성 API"
	)
	ResponseEntity<ApiResult<Void>> create(
		DefaultCurrentUser currentUser,
		ReviewRequest.Create request
	);

	@Operation(
		summary = "내가 작성한 리뷰 목록 조회", description = "내가 작성한 리뷰 목록을 조회하는 API"
	)
	ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> searchMines(
		DefaultCurrentUser defaultCurrentUser,
		@ParameterObject Paging paging
	);

	@Operation(
		summary = "상품 리뷰 목록 조회", description = "특정 상품에 대한 리뷰 목록을 조회하는 API",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID")
		}
	)
	ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> search(
		UUID productId,
		@ParameterObject Paging paging
	);

	@Operation(
		summary = "리뷰 수정", description = "리뷰 수정 API",
		parameters = {
			@Parameter(name = "reviewId", description = "리뷰 ID")
		}
	)
	@PatchMapping("/{reviewId}")
	ResponseEntity<ApiResult<Void>> update(
		DefaultCurrentUser currentUser,
		UUID reviewId,
		ReviewRequest.Update request
	);

	@Operation(
		summary = "리뷰 삭제",
		description = "리뷰 삭제 API",
		parameters = {
			@Parameter(name = "reviewId", description = "리뷰 ID")
		}
	)
	@DeleteMapping("/{reviewId}")
	ResponseEntity<ApiResult<Void>> delete(
		DefaultCurrentUser currentUser,
		UUID reviewId
	);
}
