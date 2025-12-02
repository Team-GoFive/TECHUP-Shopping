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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {
	private final CourierService courierService;

	@GetMapping("/{courierId}")
	public ResponseEntity<ApiResult<CourierResponse.Detail>> getCourierDetail(
		@PathVariable UUID courierId
	){
		return ApiResult.wrap(
			courierService.getDetail(courierId)
		);
	}

	@PutMapping("/{courierId}")
	public ResponseEntity<ApiResult<Void>> updateCourier(
		@PathVariable UUID courierId,
		@RequestBody CourierRequest.UpdateDetails request
	){
		courierService.updateDetail(courierId, request);
		return ApiResult.empty();
	}
}
