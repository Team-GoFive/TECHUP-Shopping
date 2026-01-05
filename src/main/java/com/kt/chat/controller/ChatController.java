package com.kt.chat.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.kt.chat.domain.dto.ChatRequest;
import com.kt.chat.domain.dto.ChatResponse;
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
		@Payload ChatRequest.Message request,
		Principal principal
	) {
		Authentication authentication = (Authentication)principal;
		DefaultCurrentUser currentUser =
			(DefaultCurrentUser)authentication.getPrincipal();

		UUID senderId = currentUser.getId();
		String role = currentUser.getRole().name();

		String message = request.message();

		chatMessageService.save(conversationId, senderId, role, message);

		messagingTemplate.convertAndSend(
			"/sub/chat/" + conversationId,
			new ChatResponse.Message(
				senderId,
				role,
				message
			)
		);
	}
}