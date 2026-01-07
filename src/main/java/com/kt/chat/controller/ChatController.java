package com.kt.chat.controller;

import static com.kt.common.api.ApiResult.*;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.kt.chat.domain.dto.ChatRequest;
import com.kt.chat.domain.dto.ChatResponse;
import com.kt.chat.service.ChatMessageService;
import com.kt.chat.service.ChatRoomService;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController implements ChatSwaggerSupporter {

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatMessageService chatMessageService;
	private final ChatRoomService chatRoomService;

	@Override
	@PostMapping("/api/chat/apply")
	public ResponseEntity<ApiResult<Void>> applyChat(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody ChatRequest.ApplyChat request
	) {
		chatRoomService.createOrWaiting(
			UUID.fromString(request.conversationId()),
			defaultCurrentUser.getId()
		);

		return empty();
	}

	@GetMapping("/api/chat/{conversationId}/messages")
	public ResponseEntity<ApiResult<PageResponse<ChatResponse.Search>>> messages(
		@PathVariable UUID conversationId,
		@RequestParam(required = false) Instant cursor,
		Pageable pageable
	) {
		return page(chatMessageService.getMessages(
			conversationId,
			cursor,
			pageable
		));
	}

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