package com.kt.chat.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.chat.service.AdminChatRoomService;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/chat")
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

}