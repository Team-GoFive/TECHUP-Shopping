package com.kt.controller.cart;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.CartRequest;
import com.kt.domain.dto.response.CartResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cart", description = "장바구니 관련 API")
public interface CartSwaggerSupporter extends SwaggerSupporter {

	@Operation(summary = "장바구니 조회 API")
	ResponseEntity<CartResponse.Cart> getCart(
		@Parameter(hidden = true) DefaultCurrentUser currentUser
	);

	@Operation(summary = "장바구니 상품 추가 API")
	ResponseEntity<Void> addItem(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		CartRequest.AddItem request
	);

	@Operation(summary = "장바구니 상품 수량 변경 API")
	ResponseEntity<Void> changeQuantity(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		UUID cartItemId,
		CartRequest.UpdateQuantity request
	);

	@Operation(summary = "장바구니 상품 삭제 API")
	ResponseEntity<Void> removeItem(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		UUID cartItemId
	);

	@Operation(summary = "장바구니 전체 비우기 API")
	ResponseEntity<Void> clear(
		@Parameter(hidden = true) DefaultCurrentUser currentUser
	);
}
