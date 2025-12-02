package com.kt.controller.admin.product;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.CurrentUser;
import com.kt.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<ApiResult<Void>> create(
		@RequestBody @Valid AdminProductRequest.Create request
	) {
		productService.create(
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId()
		);
		return empty();
	}

	@PatchMapping("/sold-out")
	public ResponseEntity<ApiResult<Void>> soldOutProducts(
		@RequestBody @Valid AdminProductRequest.SoldOut request
	) {
		productService.soldOutProducts(request.productIds());
		return empty();
	}

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@AuthenticationPrincipal CurrentUser user,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type,
		Paging paging
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
	public ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		@AuthenticationPrincipal CurrentUser user,
		@PathVariable UUID productId
	) {
		return wrap(productService.detail(user.getRole(), productId));
	}

	@PatchMapping("/{productId}/toggle-sold-out")
	public ResponseEntity<ApiResult<Void>> toggleActive(
		@PathVariable UUID productId
	) {
		productService.toggleActive(productId);
		return empty();
	}

	@PatchMapping("/{productId}/activate")
	public ResponseEntity<ApiResult<Void>> activate(
		@PathVariable UUID productId
	) {
		productService.activate(productId);
		return empty();
	}

	@PatchMapping("/{productId}/in-activate")
	public ResponseEntity<ApiResult<Void>> inActivate(
		@PathVariable UUID productId
	) {
		productService.inActivate(productId);
		return empty();
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ApiResult<Void>> update(
		@PathVariable UUID productId,
		@RequestBody @Valid AdminProductRequest.Update request
	) {
		productService.update(
			productId,
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId()
		);
		return empty();
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@PathVariable UUID productId
	) {
		productService.delete(productId);
		return empty();
	}

}
