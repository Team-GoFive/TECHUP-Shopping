package com.kt.controller.admin.courier;

import java.util.UUID;

import com.kt.common.support.SwaggerAssistanceInterface;
import com.kt.domain.dto.response.CourierResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "courier", description = "배송기사 관련 API")
public interface AdminCourierSwaggerSupporter extends SwaggerAssistanceInterface {
	@Operation(summary = "배송기사 상세 조회", description = "배송기사 정보를 상세 조회 합니다.", parameters = {
		@Parameter(name = "courierId", description = "계정 Id")})
	CourierResponse.DetailAdmin getCourierDetail(UUID courierId);
}
