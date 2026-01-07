package com.kt.controller.admin.product;

import java.util.UUID;

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
public interface AdminProductSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "상품 리스트 조회",
		description = "관리자의 상품 조회 관련 API",
		parameters = {
			@Parameter(name = "keyword", description = "검색 키워드"),
		}
	)
	ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@Parameter(hidden = true) CurrentUser user,
		String keyword,
		ProductSearchType type,
		Paging paging
	);

	@Operation(
		summary = "상품 삭제",
		description = "관리자의 상품 삭제 관련 API",
		parameters = {
			@Parameter(name = "productId", description = "상품ID")
		}
	)
	ResponseEntity<ApiResult<Void>> delete(UUID productId);
}
