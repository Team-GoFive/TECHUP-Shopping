package com.kt.controller.order;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@GetMapping
	ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrders(
		@ModelAttribute Paging paging
	) {
		return ApiResult.page(
			orderService.searchOrder(paging.toPageable())
		);
	}

	@GetMapping("/{orderId}")
	ResponseEntity<ApiResult<OrderResponse.OrderProducts>> getOrderDetail(
		@PathVariable UUID orderId
	) {
		return ApiResult.wrap(
			orderService.getOrderProducts(orderId)
		);
	}

	@PostMapping
	ResponseEntity<ApiResult<Void>> createOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@Valid @RequestBody OrderRequest request
	) {
		orderService.createOrder(
			currentUser.getUsername(),
			request.items(),
			request.addressId()
		);
		return empty();
	}

	@PatchMapping("/{orderId}/cancel")
	ResponseEntity<ApiResult<Void>> cancelOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID orderId
	) {
		orderService.cancelOrder(currentUser.getId(), orderId);
		return empty();
	}

	@PutMapping("/{orderId}")
	ResponseEntity<ApiResult<Void>> updateOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID orderId,
		@Valid @RequestBody OrderRequest.Update request
	) {
		orderService.updateOrder(currentUser.getId(), orderId, request);
		return empty();
	}

}
