package com.kt.controller.cart;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.CartRequest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.CartResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.CartService;
import com.kt.service.OrderApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController implements CartSwaggerSupporter {

	private final CartService cartService;
	private final OrderApplicationService orderApplicationService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<CartResponse.Cart>> getCart(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		return ApiResult.wrap(
			cartService.getCartView(currentUser.getId())
		);
	}

	@Override
	@PostMapping("/items")
	public ResponseEntity<ApiResult<Void>> addItem(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid CartRequest.AddItem request
	) {
		cartService.addItem(
			currentUser.getId(),
			request.productId(),
			request.quantity()
		);

		return ApiResult.empty();
	}

	@Override
	@PatchMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResult<Void>> changeQuantity(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID cartItemId,
		@RequestBody @Valid CartRequest.UpdateQuantity request
	) {
		cartService.changeQuantity(
			currentUser.getId(),
			cartItemId,
			request.quantity()
		);

		return ApiResult.empty();
	}

	@Override
	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResult<Void>> removeItem(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID cartItemId
	) {
		cartService.removeItem(
			currentUser.getId(),
			cartItemId
		);

		return ApiResult.empty();
	}

	@Override
	@DeleteMapping
	public ResponseEntity<ApiResult<Void>> clear(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		cartService.clear(currentUser.getId());

		return ApiResult.empty();
	}

	@Override
	@PostMapping("/orders")
	public ResponseEntity<ApiResult<Void>> createOrderFromCart(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid OrderRequest.CartOrderRequest request
	) {
		orderApplicationService.createOrderFromCart(
			currentUser.getId(),
			request
		);

		return ApiResult.empty();
	}
}