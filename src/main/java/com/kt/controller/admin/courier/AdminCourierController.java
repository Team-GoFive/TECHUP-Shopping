package com.kt.controller.admin.courier;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.service.CourierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/couriers")
public class AdminCourierController {

	private final CourierService courierService;

	@GetMapping("/{courierId}")
	public ResponseEntity<ApiResult<CourierResponse.DetailAdmin>> getCourierDetail(
		@PathVariable UUID courierId
	) {
		return wrap(courierService.getDetailForAdmin(courierId));
	}
}
