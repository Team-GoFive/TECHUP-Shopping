package com.kt.controller.admin.management;

import com.kt.domain.dto.request.AdminRequest;

import com.kt.domain.dto.response.AdminResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.service.admin.AdminManagementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminManagementController implements AdminManagementSwaggerSupporter {

	private final AdminManagementService adminManagementService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<AdminResponse.Detail>> me() {
		return wrap(
			adminManagementService.detail()
		);
	}

	@Override
	@PutMapping
	public ResponseEntity<ApiResult<Void>> update(
		@RequestBody @Valid AdminRequest.Update request
	) {
		adminManagementService.update(request);
		return empty();
	}

}
