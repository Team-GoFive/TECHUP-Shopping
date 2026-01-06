package com.kt.controller.cart;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.CartRequest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.CartResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cart", description = "장바구니 관련 API")
public interface CartSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "장바구니 조회",
		description = "사용자가 본인의 장바구니를 조회하는 API"
	)
	ResponseEntity<ApiResult<CartResponse.Cart>> getCart(
		@Parameter(hidden = true) DefaultCurrentUser currentUser
	);

	@Operation(
		summary = "장바구니 상품 추가",
		description = "사용자가 장바구니에 상품을 담는 API"
	)
	ResponseEntity<ApiResult<Void>> addItem(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		CartRequest.AddItem request
	);

	@Operation(
		summary = "장바구니 상품 수량 변경",
		description = "사용자가 장바구니에 담긴 상품 수량을 변경하는 API"
	)
	ResponseEntity<ApiResult<Void>> changeQuantity(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		UUID cartItemId,
		CartRequest.UpdateQuantity request
	);

	@Operation(
		summary = "장바구니 상품 삭제",
		description = "사용자가 장바구니에 담긴 상품을 삭제하는 API"
	)
	ResponseEntity<ApiResult<Void>> removeItem(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		UUID cartItemId
	);

	@Operation(
		summary = "장바구니 전체 비우기",
		description = "사용자가 장바구니 상품을 전체 삭제하는 API"
	)
	ResponseEntity<ApiResult<Void>> clear(
		@Parameter(hidden = true) DefaultCurrentUser currentUser
	);

	@Operation(
		summary = "장바구니 선택 상품 주문 생성 API",
		description = "사용자가 장바구니에서 상품을 선택하여 주문하는 API"
	)
	ResponseEntity<ApiResult<Void>> createOrder(
		@Parameter(hidden = true) DefaultCurrentUser currentUser,
		OrderRequest.CartOrder request
	);
}
