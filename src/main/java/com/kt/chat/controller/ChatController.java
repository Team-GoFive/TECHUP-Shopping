package com.kt.chat.controller;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.kt.chat.service.ChatMessageService;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageService chatMessageService;

	@MessageMapping("/chat/{conversationId}")
	public void handleChat(
		@DestinationVariable UUID conversationId,
		@Payload String message,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	) {
		UUID senderId = defaultCurrentUser.getId();
		String role = defaultCurrentUser.getRole().name();

		chatMessageService.save(conversationId, senderId, role, message);

		messagingTemplate.convertAndSend("/sub/chat/" + conversationId, message);
	}

}
