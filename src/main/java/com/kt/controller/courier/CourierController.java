package com.kt.controller.courier;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.CourierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController implements CourierSwaggerSupporter {
	private final CourierService courierService;

	@Override
	@GetMapping("/{courierId}")
	public ResponseEntity<ApiResult<CourierResponse.Detail>> getCourierDetail(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID courierId
	){
		return ApiResult.wrap(
			courierService.getDetail(defaultCurrentUser.getId(), courierId)
		);
	}

	@Override
	@PutMapping("/{courierId}")
	public ResponseEntity<ApiResult<Void>> updateCourier(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID courierId,
		@RequestBody CourierRequest.UpdateDetails request
	){
		courierService.updateDetail(defaultCurrentUser.getId(), courierId, request);
		return ApiResult.empty();
	}
}
