package com.kt.chat.domain.dto;

import java.util.UUID;

public class ChatResponse {
	public record Message(
		UUID senderId,
		String senderRole,
		String message
	) {

	}
}
