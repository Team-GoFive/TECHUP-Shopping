package com.kt.controller.admin.order;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.response.AdminOrderResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.admin.AdminOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController implements AdminOrderSwaggerSupporter {
	public final AdminOrderService adminOrderService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrder(
		@ModelAttribute Paging paging
	) {
		return page(adminOrderService.searchOrder(
			paging.toPageable()
		));
	}

	@Override
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResult<AdminOrderResponse.Detail>> getOrderDetail(
		@PathVariable UUID orderId
	) {
		return wrap(adminOrderService.getOrderDetail(orderId));
	}

	// TODO: 정책 수정 필요
	// @Override
	// @PatchMapping("/{orderProductId}/change-status")
	// public ResponseEntity<ApiResult<Void>> forceChangeOrderProductStatus(
	// 	@PathVariable UUID orderProductId,
	// 	@RequestBody OrderProductRequest.ChangeStatus request
	// ) {
	// 	adminOrderService.updateOrderProductStatus(orderProductId, request.status());
	// 	return empty();
	// }

	@Override
	@PatchMapping("/order-products/{orderProductId}/cancel")
	public ResponseEntity<ApiResult<Void>> cancelOrderProduct(
		@PathVariable UUID orderProductId,
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		adminOrderService.cancelOrder(currentUser.getId(), orderProductId);
		return empty(); // TODO: cancelOrderProduct로 변경
	}
}
