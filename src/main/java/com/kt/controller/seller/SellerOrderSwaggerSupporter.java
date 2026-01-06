package com.kt.controller.seller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.SellerOrderResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order", description = "판매자 주문 관련 API")
public interface SellerOrderSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "판매자 주문상품 목록 조회",
		description = "판매자가 주문상품 목록을 조회하는 API"
	)
	@GetMapping
	ResponseEntity<ApiResult<PageResponse<SellerOrderResponse.Search>>> searchOrderProducts(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,

		@Parameter(description = "주문상품 ID")
		@RequestParam(required = false)
		UUID orderProductId,

		@Parameter(description = "주문상품 상태")
		@RequestParam(required = false)
		OrderProductStatus status,

		@Parameter(description = "페이징 정보")
		@ParameterObject
		Paging paging
	);

	@Operation(
		summary = "주문상품 취소 처리",
		description = "판매자가 접수된 주문을 취소 처리하는 API"
	)
	@PatchMapping("/{orderProductId}/cancel")
	ResponseEntity<ApiResult<Void>> cancelOrderProduct(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,

		@Parameter(description = "주문상품 ID", required = true)
		@PathVariable
		UUID orderProductId
	);

	@Operation(
		summary = "주문상품 결제 승인",
		description = "판매자가 결제 완료된 주문상품을 승인 처리하는 API"
	)
	@PatchMapping("/{orderProductId}/confirm")
	ResponseEntity<ApiResult<Void>> confirmPaidOrderProduct(
		@Parameter(hidden = true)
		DefaultCurrentUser currentSeller,

		@Parameter(description = "주문상품 ID", required = true)
		@PathVariable
		UUID orderProductId
	);
}
