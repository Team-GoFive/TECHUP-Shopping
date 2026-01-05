package com.kt.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.ai.dto.request.FAQRequest;
import com.kt.ai.dto.response.FAQResponse;
import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FAQ", description = "FAQ 관련 Api")
public interface FAQSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "FAQ 챗봇 질문",
		description = "FAQ 챗봇에 질문합니다."
	)
	ResponseEntity<ApiResult<FAQResponse.ChatBot>> askFAQ(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody FAQRequest.AskFAQ request
	);
}
