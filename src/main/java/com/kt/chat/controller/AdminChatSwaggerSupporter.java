package com.kt.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import com.kt.chat.domain.dto.AdminChatRequest;
import com.kt.chat.domain.dto.ChatRoomInfoResponse;
import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Chat", description = "채팅 관련 apI")
public interface AdminChatSwaggerSupporter extends SwaggerSupporter {
	@Operation(
		summary = "관리자 채팅방 진입",
		description = "관리자가 채팅방을 준비 상태로 변경합니다."
	)
	ResponseEntity<ApiResult<Void>> accept(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody AdminChatRequest.AcceptChat request
	);

	@Operation(
		summary = "전체 채팅방 조회",
		description = "관리자가 전체 채팅방을 조회합니다."
	)
	ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getAllRooms();

	@Operation(
		summary = "대기중 채팅방 조회",
		description = "관리자가 응답 대기중인 채팅방을 조회합니다."
	)
	ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getWaitingRooms();

	@Operation(
		summary = "상담중 채팅방 조회",
		description = "관리자가 상담중인 채팅방을 조회합니다."
	)
	ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getConnectedRooms();
}
