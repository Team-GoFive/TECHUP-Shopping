package com.kt.controller.admin.category;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.request.CategoryRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Category", description = "카테고리 관리 관련 API")
public interface AdminCategorySwaggerSupporter {
	@Operation(
		summary = "카테고리 생성", description = "카테고리를 생성합니다."
	)
	ResponseEntity<ApiResult<Void>> create(
		@RequestBody CategoryRequest.Create request
	);

	@Operation(
		summary = "카테고리 수정", description = "카테고리 이름을 수정합니다.",
		parameters = {
			@Parameter(name = "categoryId", description = "카테고리 Id"),
		}
	)
	ResponseEntity<ApiResult<Void>> update(
		@RequestBody CategoryRequest.Update request,
		@PathVariable UUID categoryId
	);

	@Operation(
		summary = "카테고리 삭제", description = "카테고리를 삭제합니다.",
		parameters = {
			@Parameter(name = "categoryId", description = "카테고리 Id"),
		}
	)
	ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID categoryId
	);

}
