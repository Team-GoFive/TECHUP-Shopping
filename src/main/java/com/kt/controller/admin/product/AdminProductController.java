package com.kt.controller.admin.product;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.CurrentUser;
import com.kt.service.admin.AdminProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController implements AdminProductSwaggerSupporter {

	private final AdminProductService adminProductService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@AuthenticationPrincipal CurrentUser user,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type,
		Paging paging
	) {
		return page(
			adminProductService.search(
				user.getRole(),
				keyword,
				type,
				paging.toPageable()
			)
		);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		@AuthenticationPrincipal CurrentUser user,
		@PathVariable UUID productId
	) {
		return wrap(adminProductService.detail(user.getRole(), productId));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID productId
	) {
		adminProductService.delete(productId);
		return empty();
	}

}
