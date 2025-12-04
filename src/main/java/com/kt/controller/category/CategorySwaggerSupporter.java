package com.kt.controller.category;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.response.CategoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Category", description = "카테고리 조회 API")
public interface CategorySwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "전체 카테고리 조회",
		description = "전체 카테고리를 트리 구조로 조회하는 API"
	)
	ResponseEntity<ApiResult<List<CategoryResponse.CategoryTreeItem>>> getAllCategories();

}