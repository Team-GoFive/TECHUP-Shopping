package com.kt.controller.adminorder;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.constant.OrderStatus;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
	public final OrderService orderService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrder(
		@ModelAttribute Paging paging
	) {
		return page(orderService.searchOrder(
			paging.toPageable()
		));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResult<AdminOrderResponse.Detail>> getOrderDetail(
		@PathVariable UUID id
	) {
		return wrap(orderService.getOrderDetail(id));
	}

	@PutMapping("/{id}/change-status")
	public ResponseEntity<ApiResult<Void>> updateOrderStatus(
		@PathVariable UUID id,
		@RequestBody OrderStatus newStatus
	) {
		orderService.updateOrderStatus(id, newStatus);
		return empty();
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<ApiResult<Void>> cancelOrder(
		@PathVariable UUID id
	) {
		orderService.cancelOrder(id);
		return empty();
	}
}
