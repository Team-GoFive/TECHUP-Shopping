package com.kt.controller.courier;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Courier", description = "배송기사 관련 API")
public interface CourierSwaggerSupporter extends SwaggerSupporter{

	@Operation(
		summary = "배송기사 상세 조회",
		description = "배송기사의 상세정보 조회 관련 API"
		, parameters = {
			@Parameter(name = "courierId" , description = "배송기사 ID")
		}
	)
	ResponseEntity<ApiResult<CourierResponse.Detail>> getCourierDetail(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		UUID courierId
	);

	@Operation(
		summary = "배송기사 정보 수정",
		description = "배송기사의 정보 수정 관련 API"
		, parameters = {
			@Parameter(name = "courierId" , description = "배송기사 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> updateCourier(
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		UUID courierId,
		CourierRequest.UpdateDetails request
	);


}
