package com.kt.controller.admin.courier;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.admin.AdminCourierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/couriers")
public class AdminCourierController implements AdminCourierSwaggerSupporter {

	private final AdminCourierService adminCourierService;

	@Override
	@GetMapping("/{courierId}")
	public ResponseEntity<ApiResult<CourierResponse.DetailAdmin>> getCourierDetail(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID courierId
	) {
		return wrap(adminCourierService.getDetail(defaultCurrentUser.getId(), courierId));
	}
}
