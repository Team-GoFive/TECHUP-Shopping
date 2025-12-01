package com.kt.controller.admin;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.CategoryRequest;
import com.kt.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

	private final CategoryService categoryService;

	@PostMapping
	ResponseEntity<ApiResult<Void>> create(
		@RequestBody CategoryRequest.Create request
	) {
		categoryService.create(
			request.title(),
			request.parentId()
		);
		return empty();
	}

	@PutMapping("/{categoryId}")
	ResponseEntity<ApiResult<Void>> update(
		@RequestBody CategoryRequest.Update request,
		@PathVariable UUID categoryId
	) {
		categoryService.update(
			categoryId,
			request.title()
		);
		return empty();
	}

	@DeleteMapping("/{categoryId}")
	ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID categoryId
	) {
		categoryService.delete(categoryId);
		return empty();
	}

}
