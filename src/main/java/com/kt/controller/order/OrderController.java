package com.kt.controller.order;

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
import com.kt.domain.dto.request.OrderRequest;
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
	ResponseEntity<?> searchOrders(
		@ModelAttribute Paging paging
	) {
		orderService.searchOrder(paging.toPageable());
		return ApiResult.wrap(null);
	}

	@GetMapping("/{orderId}")
	ResponseEntity<?> getOrderDetail(
		@PathVariable UUID orderId
	) {
		orderService.getOrderDetail(orderId);
		return ApiResult.wrap(null);
	}

	// TODO: 주문 생성 /api/orders
	@PostMapping
	ResponseEntity<?> createOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@Valid @RequestBody OrderRequest request
	) {
		orderService.createOrder(
			currentUser.getUsername(),
			request.items()
		);
		return ApiResult.wrap(null);
	}

	@PatchMapping("/{orderId}/cancel")
	ResponseEntity<?> cancelOrder(
		@PathVariable UUID orderId
	) {
		orderService.cancelOrder(orderId);
		return ApiResult.wrap(null);
	}

	@PutMapping("/api/orders/{orderId}")
	ResponseEntity<?> updateOrder(
		@PathVariable UUID orderId,
		@Valid @RequestBody OrderRequest.Update request
	) {
		orderService.updateOrder(orderId, request);
		return ApiResult.wrap(null);
	}

}
