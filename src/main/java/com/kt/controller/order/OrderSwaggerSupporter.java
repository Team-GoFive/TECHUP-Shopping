package com.kt.controller.order;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.OrderResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order", description = "사용자 주문 관련 API")
public interface OrderSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "주문 목록 조회",
		description = "사용자가 주문 목록을 조회하는 API"
	)
	ResponseEntity<ApiResult<PageResponse<OrderResponse.Search>>> searchOrders(
		Paging paging
	);

	@Operation(
		summary = "주문 상세 조회",
		description = "사용자가 주문 상세 정보를 조회하는 API",
		parameters = {
			@Parameter(name = "orderId", description = "주문 ID")
		}
	)
	ResponseEntity<ApiResult<OrderResponse.Detail>> getOrderDetail(
		UUID orderId
	);

	@Operation(
		summary = "주문 생성",
		description = "사용자가 주문을 생성하는 API"
	)
	ResponseEntity<ApiResult<Void>> createOrder(
		DefaultCurrentUser currentUser,
		OrderRequest.Create request
	);

	@Operation(
		summary = "주문 및 결제 생성",
		description = "사용자가 주문생성과 동시에 주문 결제하는 API"
	)
	ResponseEntity<ApiResult<Void>> orderPay(
		DefaultCurrentUser currentUser,
		OrderRequest.Create request
	);

	@Operation(
		summary = "주문 취소",
		description = "사용자가 자신의 주문을 주문 상품별로 취소하는 API",
		parameters = {
			@Parameter(name = "orderProductId", description = "주문 상품 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> cancelOrderProduct(
		UUID orderProductId,
		DefaultCurrentUser currentUser
	);

	@Operation(
		summary = "주문 정보 수정",
		description = "사용자가 주문의 수령인 및 주소 정보를 수정하는 API",
		parameters = {
			@Parameter(name = "orderId", description = "주문 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> changeOrderAddress(
		DefaultCurrentUser currentUser,
		UUID orderId,
		OrderRequest.Update request
	);

}