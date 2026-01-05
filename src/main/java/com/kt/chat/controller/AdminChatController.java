package com.kt.chat.controller;

import static com.kt.common.api.ApiResult.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.chat.domain.dto.AdminChatRequest;
import com.kt.chat.domain.dto.ChatRoomInfoResponse;
import com.kt.chat.service.AdminChatRoomService;
import com.kt.common.api.ApiResult;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/chat/rooms")
@RequiredArgsConstructor

public class AdminChatController implements AdminChatSwaggerSupporter {

	private final AdminChatRoomService chatRoomService;

	@Override
	@PostMapping("/handover/accept")
	public ResponseEntity<ApiResult<Void>> accept(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody AdminChatRequest.AcceptChat request
	) {
		chatRoomService.accept(request.conversationId(), defaultCurrentUser.getId());
		return empty();
	}

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getAllRooms() {
		return wrap(chatRoomService.getAll());
	}

	@Override
	@GetMapping("/waiting")
	public ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getWaitingRooms() {
		return wrap(chatRoomService.getWaitingRooms());
	}

	@Override
	@GetMapping("/connected")
	public ResponseEntity<ApiResult<List<ChatRoomInfoResponse>>> getConnectedRooms() {
		return wrap(chatRoomService.getConnectedRooms());
	}

}