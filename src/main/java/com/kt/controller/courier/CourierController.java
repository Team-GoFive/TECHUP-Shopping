package com.kt.controller.courier;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.service.CourierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Courier", description = "Courier API")
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {
	private final CourierService courierService;

	@Operation(
		summary = "배송기사 상세 조회",
		description = "배송기사의 상세정보 조회 관련 API"
		, parameters = {
			@Parameter(name = "courierId" , description = "배송기사 ID")
		}
	)
	@GetMapping("/{courierId}")
	public ResponseEntity<ApiResult<CourierResponse.Detail>> getCourierDetail(
		@PathVariable UUID courierId
	){
		return ApiResult.wrap(
			courierService.getDetail(courierId)
		);
	}


	@Operation(
		summary = "배송기사 정보 수정",
		description = "배송기사의 정보 수정 관련 API"
		, parameters = {
			@Parameter(name = "courierId" , description = "배송기사 ID"),
			@Parameter(name = "CourierRequest.UpdateDetails" , description = "배송기사 정보를 수정하는 필드를 포함하는 DTO")
		}
	)
	@PutMapping("/{courierId}")
	public ResponseEntity<ApiResult<Void>> updateCourier(
		@PathVariable UUID courierId,
		@RequestBody CourierRequest.UpdateDetails request
	){
		courierService.updateDetail(courierId, request);
		return ApiResult.empty();
	}
}
