package com.kt.chat.domain.dto;

import java.util.UUID;

public class AdminChatRequest {
	public record AcceptChat(
		UUID conversationId
	) {

	}
}
