package com.kt.common.support;

import com.kt.common.api.ApiErrorResponse;
import com.kt.common.api.ApiResult;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
	@ApiResponse(
		responseCode = "200",
		description = "요청 성공",
		content = @Content(schema = @Schema(implementation = ApiResult.class))
	),

	@ApiResponse(
		responseCode = "400",
		description = "잘못된 요청입니다.",
		content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
	),

	@ApiResponse(
		responseCode = "500",
		description = "서버 에러 - 관리자에게 문의하세요.",
		content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
	)
})
public interface SwaggerAssistanceInterface {
}
