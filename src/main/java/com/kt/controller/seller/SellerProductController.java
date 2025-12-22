package com.kt.controller.seller;

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
import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.seller.SellerProductService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController implements SellerProductSwaggerController {
	private final SellerProductService sellerProductService;

	@Override
	@PostMapping
	public ResponseEntity<ApiResult<Void>> create(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid SellerProductRequest.Create request
	) {
		sellerProductService.create(
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId(),
			defaultCurrentUser.getId()
		);
		return empty();
	}

	@Override
	@PatchMapping("/sold-out")
	public ResponseEntity<ApiResult<Void>> soldOutProducts(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid SellerProductRequest.SoldOut request
	) {
		sellerProductService.soldOutProducts(request.productIds(), defaultCurrentUser.getId());
		return empty();
	}

	@Override
	@PatchMapping("/{productId}/toggle-sold-out")
	public ResponseEntity<ApiResult<Void>> toggleActive(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID productId
	) {
		sellerProductService.toggleActive(productId, defaultCurrentUser.getId());
		return empty();
	}

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<ProductResponse.Search>>> search(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) ProductSearchType type,
		Paging paging
	) {
		return page(
			sellerProductService.search(
				keyword,
				type,
				paging.toPageable(),
				defaultCurrentUser.getId()
			)
		);
	}

	@Override
	@GetMapping("/{productId}")
	public ResponseEntity<ApiResult<ProductResponse.Detail>> detail(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID productId
	) {
		return wrap(sellerProductService.detail(productId, defaultCurrentUser.getId()));
	}

	@Override
	@PatchMapping("/activate")
	public ResponseEntity<ApiResult<Void>> activate(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid SellerProductRequest.Activate request
	) {
		sellerProductService.activate(request.productIds(), defaultCurrentUser.getId());
		return empty();
	}

	@Override
	@PatchMapping("/in-activate")
	public ResponseEntity<ApiResult<Void>> inactivate(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid SellerProductRequest.InActivate request
	) {
		sellerProductService.inActivate(
			request.productIds(),
			defaultCurrentUser.getId()
		);
		return empty();
	}

	@Override
	@PutMapping("/{productId}")
	public ResponseEntity<ApiResult<Void>> update(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID productId,
		@RequestBody @Valid SellerProductRequest.Update request
	) {
		sellerProductService.update(
			productId,
			request.name(),
			request.price(),
			request.stock(),
			request.categoryId(),
			defaultCurrentUser.getId()
		);
		return empty();
	}

	@Override
	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResult<Void>> delete(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID productId
	) {
		sellerProductService.delete(productId, defaultCurrentUser.getId());
		return empty();
	}
}
