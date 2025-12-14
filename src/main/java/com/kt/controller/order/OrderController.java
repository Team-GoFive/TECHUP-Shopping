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
public class OrderController implements OrderSwaggerSupporter {

	private final OrderService orderService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrders(
		@ModelAttribute Paging paging
	) {
		return ApiResult.page(
			orderService.searchOrder(paging.toPageable())
		);
	}

	@Override
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResult<OrderResponse.OrderProducts>> getOrderDetail(
		@PathVariable UUID orderId
	) {
		return ApiResult.wrap(
			orderService.getOrderProducts(orderId)
		);
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResult<Void>> createOrder(
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

	@Override
	@PatchMapping("/{orderId}/cancel")
	public ResponseEntity<ApiResult<Void>> cancelOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID orderId
	) {
		orderService.cancelOrderProduct(currentUser.getId(), orderId);
		return empty();
	}

	@Override
	@PutMapping("/{orderId}")
	public ResponseEntity<ApiResult<Void>> updateOrder(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID orderId,
		@Valid @RequestBody OrderRequest.Update request
	) {
		orderService.changeOrderAddress(currentUser.getId(), orderId, request);
		return empty();
	}

}
