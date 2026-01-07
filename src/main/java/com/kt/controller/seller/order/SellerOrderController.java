package com.kt.controller.seller.order;

import static com.kt.common.api.ApiResult.*;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.SellerOrderResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.seller.SellerOrderService;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class SellerOrderController implements SellerOrderSwaggerSupporter {

	private final SellerOrderService sellerOrderService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<SellerOrderResponse.Search>>> searchOrderProducts(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestParam(required = false) UUID orderProductId,
		@RequestParam(required = false) OrderProductStatus status,
		@Valid @ParameterObject Paging paging
	) {
		return page(
			sellerOrderService.searchOrderProducts(
				paging.toPageable(),
				status,
				orderProductId,
				defaultCurrentUser.getId()
			)
		);
	}

	@PatchMapping("/{orderProductId}/cancel")
	public ResponseEntity<ApiResult<Void>> cancelOrderProduct(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID orderProductId
	) {
		sellerOrderService.cancelOrderProduct(orderProductId, defaultCurrentUser.getId());
		return empty();
	}

	@PatchMapping("/{orderProductId}/confirm")
	public ResponseEntity<ApiResult<Void>> confirmPaidOrderProduct(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID orderProductId
	) {
		sellerOrderService.confirmPaidOrderProduct(orderProductId, defaultCurrentUser.getId());
		return empty();
	}
}
