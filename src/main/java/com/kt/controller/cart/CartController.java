package com.kt.controller.cart;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kt.domain.dto.request.CartRequest;
import com.kt.domain.dto.response.CartResponse;
import com.kt.domain.entity.CartEntity;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController implements CartSwaggerSupporter {

	private final CartService cartService;

	@Override
	@GetMapping
	public ResponseEntity<CartResponse.Cart> getCart(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		CartEntity cart = cartService.getCart(currentUser.getId());
		return ResponseEntity.ok(CartResponse.toResponse(cart));
	}

	@Override
	@PostMapping("/items")
	public ResponseEntity<Void> addItem(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid CartRequest.AddItem request
	) {
		cartService.addItem(
			currentUser.getId(),
			request.productId(),
			request.quantity()
		);

		return ResponseEntity.ok().build();
	}

	@Override
	@PatchMapping("/items/{cartItemId}")
	public ResponseEntity<Void> changeQuantity(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID cartItemId,
		@RequestBody @Valid CartRequest.UpdateQuantity request
	) {
		cartService.changeQuantity(
			currentUser.getId(),
			cartItemId,
			request.quantity()
		);

		return ResponseEntity.ok().build();
	}

	@Override
	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<Void> removeItem(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID cartItemId
	) {
		cartService.removeItem(
			currentUser.getId(),
			cartItemId
		);

		return ResponseEntity.ok().build();
	}

	@Override
	@DeleteMapping
	public ResponseEntity<Void> clear(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		cartService.clear(currentUser.getId());
		return ResponseEntity.ok().build();
	}

}
