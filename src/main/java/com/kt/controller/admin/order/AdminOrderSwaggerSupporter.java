
package com.kt.controller.admin.order;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.dto.response.AdminOrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Order", description = "관리자 주문 관련 API")
public interface AdminOrderSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "전체 주문 조회",
		description = "관리자의 전체 주문조회 관련 API"
	)
	ResponseEntity<ApiResult<PageResponse<AdminOrderResponse.Search>>> searchOrder(Paging paging);

	@Operation(
		summary = "주문 상세 조회",
		description = "관리자의 주문상세 조회 관련 API",
		parameters = {
			@Parameter(name = "orderId" , description = "주문 ID")
		}
	)
	ResponseEntity<ApiResult<AdminOrderResponse.Detail>> getOrderDetail(UUID orderId);

	@Operation(
		summary = "주문 상태 변경",
		description = "관리자의 주문상태 변경 관련 API",
		parameters = {
			@Parameter(name = "orderId" , description = "주문 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> updateOrderStatus(
		UUID orderId,
		OrderRequest.ChangeStatus request
	);

	@Operation(
		summary = "주문 취소",
		description = "관리자의 주문취소 관련 API",
		parameters = {
			@Parameter(name = "orderId" , description = "주문 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> cancelOrder(UUID orderId);
}