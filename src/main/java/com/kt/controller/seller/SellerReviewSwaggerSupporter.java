package com.kt.controller.seller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.RefundRejectRequest;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.domain.dto.response.SellerReviewResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Review", description = "판매자 리뷰 관련 API")
public interface SellerReviewSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "판매자 리뷰 목록 조회",
		description = "판매자가 자신의 상품에 대해 작성된 리뷰 목록을 조회하는 API"
	)
	ResponseEntity<ApiResult<PageResponse<SellerReviewResponse.Search>>> getAllReviews(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@Valid @ParameterObject Paging paging
	);

	@Operation(
		summary = "상품별 리뷰 목록 조회",
		description = "판매자가 자신의 상품별 리뷰 목록을 조회하는 API"
	)
	ResponseEntity<ApiResult<PageResponse<SellerReviewResponse.Search>>> getReviewsByProduct(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@Valid @ParameterObject Paging paging,
		@PathVariable UUID productId
	);

}
