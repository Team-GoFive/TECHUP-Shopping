package com.kt.controller.admin.category;

import static com.kt.common.api.ApiResult.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.CategoryRequest;
import com.kt.domain.dto.response.CategoryResponse;
import com.kt.service.admin.AdminCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController implements AdminCategorySwaggerSupporter {

	private final AdminCategoryService adminCategoryService;

	@Override
	@PostMapping
	public ResponseEntity<ApiResult<Void>> create(
		@RequestBody CategoryRequest.Create request
	) {
		adminCategoryService.create(
			request.title(),
			request.parentId()
		);
		return empty();
	}

	@Override
	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResult<Void>> update(
		@RequestBody CategoryRequest.Update request,
		@PathVariable UUID categoryId
	) {
		adminCategoryService.update(
			categoryId,
			request.title()
		);
		return empty();
	}

	@Override
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID categoryId
	) {
		adminCategoryService.delete(categoryId);
		return empty();
	}

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<List<CategoryResponse.CategoryTreeItem>>> getAllCategories() {
		List<CategoryResponse.CategoryTreeItem> list = adminCategoryService.getAll();
		return ApiResult.wrap(list);
	}

}
