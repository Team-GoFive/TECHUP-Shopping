package com.kt.controller.admin.product;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Product", description = "관리자 상품 관련 API")
public interface AdminProductSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "상품 생성",
		description = "관리자의 상품 생성 관련 API"
	)
	ResponseEntity<ApiResult<Void>> create(AdminProductRequest.Create request);

	@Operation(
		summary = "상품 품절",
		description = "관리자의 상품 품절 처리 관련 API"
	)
	ResponseEntity<ApiResult<Void>> soldOutProducts(AdminProductRequest.SoldOut request);

	@Operation(
		summary = "상품 리스트 조회",
		description = "관리자의 상품 조회 관련 API",
		parameters = {
			@Parameter(name = "keyword" , description = "검색 키워드"),
			@Parameter(name = "type" , description = "상품 / 카테고리 검색 여부 선택")
		}
	)
	ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@Parameter(hidden = true) CurrentUser user,
		String keyword,
		ProductSearchType type,
		Paging paging
	);

	@Operation(
		summary = "상품 상세 조회",
		description = "관리자의 상품 상세 조회 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<ProductResponse.Detail>> detail(@Parameter(hidden = true) CurrentUser user, UUID productId);

	@Operation(
		summary = "상품 품절 토글",
		description = "관리자의 상품 상태 ( 판매중( ACTIVATED ) / 판매중지( IN_ACTIVATED ) ) 토글 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> toggleActive(UUID productId);

	@Operation(
		summary = "상품 활성화",
		description = "관리자의 상품 상태 판매중( ACTIVATED ) 전환 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> activate(UUID productId);

	@Operation(
		summary = "상품 비활성화",
		description = "관리자의 상품 상태 판매중지( IN_ACTIVATED ) 전환 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> inActivate(UUID productId);

	@Operation(
		summary = "상품 수정",
		description = "관리자의 상품 수정 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> update(UUID productId,AdminProductRequest.Update request);

	@Operation(
		summary = "상품 삭제",
		description = "관리자의 상품 삭제 관련 API",
		parameters = {
			@Parameter(name = "productId" , description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> delete(UUID productId);
}
