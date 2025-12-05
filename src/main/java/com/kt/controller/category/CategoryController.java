package com.kt.controller.category;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.response.CategoryResponse;
import com.kt.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController implements CategorySwaggerSupporter{

	private final CategoryService categoryService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<List<CategoryResponse.CategoryTreeItem>>> getAllCategories() {
		List<CategoryResponse.CategoryTreeItem> list = categoryService.getAll();
		return ApiResult.wrap(list);
	}
}
