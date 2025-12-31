package com.kt.chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.chat.domain.dto.ChatRoomInfoResponse;
import com.kt.chat.service.AdminChatRoomService;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/chat/rooms")
@RequiredArgsConstructor

public class AdminChatController {

	private final AdminChatRoomService chatRoomService;

	@PostMapping("/handover/accept")
	public void accept(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody UUID conversationId
	) {
		chatRoomService.accept(conversationId, defaultCurrentUser.getId());
	}

	@GetMapping
	public List<ChatRoomInfoResponse> getAllRooms() {
		return chatRoomService.getAll();
	}

	@GetMapping("/waiting")
	public List<ChatRoomInfoResponse> getWaitingRooms() {
		return chatRoomService.getWaitingRooms();
	}

	@GetMapping("/connected")
	public List<ChatRoomInfoResponse> getConnectedRooms() {
		return chatRoomService.getConnectedRooms();
	}

}