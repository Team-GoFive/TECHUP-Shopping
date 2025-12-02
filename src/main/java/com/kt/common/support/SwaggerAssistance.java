package com.kt.common.support;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses(value = {
	@ApiResponse(responseCode = "200", description = "요청 성공"),
	@ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
	@ApiResponse(responseCode = "500", description = "서버에러 - 관리자에게 문의하세요.")
})
public abstract class SwaggerAssistance {
}
