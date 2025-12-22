package com.kt.controller.seller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Seller Product", description = "판매자 상품 관련 API")
public interface SellerProductSwaggerController extends SwaggerSupporter {

	@Operation(
		summary = "상품 생성",
		description = "판매자가 상품을 생성합니다."
	)
	ResponseEntity<ApiResult<Void>> create(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		@RequestBody @Valid
		SellerProductRequest.Create request
	);

	@Operation(
		summary = "상품 품절 처리",
		description = "판매자가 본인 상품을 품절 상태로 변경합니다."
	)
	ResponseEntity<ApiResult<Void>> soldOutProducts(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		@RequestBody @Valid
		SellerProductRequest.SoldOut request
	);

	@Operation(
		summary = "상품 리스트 조회",
		description = "판매자의 상품 목록을 조회합니다.",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드"),
		}
	)
	ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		String keyword,
		ProductSearchType type,
		Paging paging
	);

	@Operation(
		summary = "상품 상세 조회",
		description = "판매자의 상품 상세 정보를 조회합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId
	);

	@Operation(
		summary = "상품 활성 상태 토글",
		description = "상품 상태를 활성 / 비활성으로 토글합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<Void>> toggleActive(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId
	);

	@Operation(
		summary = "상품 활성화",
		description = "상품 상태를 판매중(ACTIVATED)으로 변경합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<Void>> activate(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId
	);

	@Operation(
		summary = "상품 비활성화",
		description = "상품 상태를 판매중지(IN_ACTIVATED)로 변경합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<Void>> inactivate(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId
	);

	@Operation(
		summary = "상품 수정",
		description = "판매자가 상품 정보를 수정합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<Void>> update(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId,

		@RequestBody @Valid
		SellerProductRequest.Update request
	);

	@Operation(
		summary = "상품 삭제",
		description = "판매자가 상품을 삭제합니다.",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID", required = true)
		}
	)
	ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal
		@Parameter(hidden = true)
		DefaultCurrentUser defaultCurrentUser,

		UUID productId
	);
}
