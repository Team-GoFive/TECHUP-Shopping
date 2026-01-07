package com.kt.ai.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.ai.dto.request.AdminFAQRequest;
import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FAQ", description = "FAQ 관련 API")
public interface FAQAdminSwaggerSupporter extends SwaggerSupporter {
	@Operation(
		summary = "FAQ 질문 답변 등록",
		description = "FAQ 질문 답변을 생성하고 벡터스토어에 업로드합니다."
	)
	ResponseEntity<ApiResult<Void>> createFAQ(
		AdminFAQRequest.Create request
	) throws Exception;

	@Operation(
		summary = "FAQ 질문 답변 삭제",
		description = "FAQ 내 질문 답변 삭제합니다."
	)
	ResponseEntity<ApiResult<Void>> deleteFAQ(
		@Schema(
			description = "FAQ ID"
		)
		UUID faqId
	);

}
