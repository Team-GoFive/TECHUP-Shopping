package com.kt.controller.product;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product", description = "상품 관련 API")
public interface ProductSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "상품 목록 조회", description = "상품 목록을 조회하는 API",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드"),
			@Parameter(name = "type", description = "검색 타입 (NAME: 상품명, CATEGORY: 카테고리)"),
		}
	)
	ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		CurrentUser user,
		String keyword,
		ProductSearchType type,
		@ParameterObject Paging paging
	);

	@Operation(
		summary = "상품 상세 조회", description = "상품의 상세 정보를 조회하는 API",
		parameters = {
			@Parameter(name = "productId", description = "상품 ID")
		}
	)
	ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		CurrentUser user,
		UUID productId
	);
}
