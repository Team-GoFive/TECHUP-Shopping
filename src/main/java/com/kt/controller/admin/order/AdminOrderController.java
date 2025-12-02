package com.kt.controller.admin.order;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController extends SwaggerAssistance implements AdminOrderSwaggerSupporter {
	public final OrderService orderService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrder(
		@ModelAttribute Paging paging
	) {
		return page(orderService.searchOrder(
			paging.toPageable()
		));
	}

	@Override
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResult<AdminOrderResponse.Detail>> getOrderDetail(
		@PathVariable UUID orderId
	) {
		return wrap(orderService.getOrderDetail(orderId));
	}

	@Override
	@PatchMapping("/{orderId}/change-status")
	public ResponseEntity<ApiResult<Void>> updateOrderStatus(
		@PathVariable UUID orderId,
		@RequestBody OrderRequest.ChangeStatus request
	) {
		orderService.updateOrderStatus(orderId, request.status());
		return empty();
	}

	@Override
	@PatchMapping("/{orderId}/cancel")
	public ResponseEntity<ApiResult<Void>> cancelOrder(
		@PathVariable UUID orderId
	) {
		orderService.cancelOrder(orderId);
		return empty();
	}
}
