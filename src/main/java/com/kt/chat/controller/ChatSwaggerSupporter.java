package com.kt.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.chat.domain.dto.ChatRequest;
import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Chat", description = "채팅 관련 API")
public interface ChatSwaggerSupporter extends SwaggerSupporter {
	@Operation(
		summary = "AI -> 관리자 상담 전환",
		description = "유저가 관리자를 대기하는 상태의 채팅방을 생성합니다."
	)
	ResponseEntity<ApiResult<Void>> applyChat(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody ChatRequest.ApplyChat request
	);
}
