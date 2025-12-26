package com.kt.controller.product;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.kt.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController implements ProductSwaggerSupporter {

	private final ProductService productService;

	@GetMapping
	@Override
	public ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@AuthenticationPrincipal CurrentUser user,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type,
		@Valid @ModelAttribute Paging paging
	) {
		return page(
			productService.search(
				user.getRole(),
				keyword,
				type,
				paging.toPageable()
			)
		);
	}

	@GetMapping("/{productId}")
	@Override
	public ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		@AuthenticationPrincipal CurrentUser user,
		@PathVariable UUID productId
	) {
		return wrap(
			productService.detail(user.getRole(), productId)
		);
	}

}
