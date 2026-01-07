package com.kt.chat.domain.dto;

public class ChatRequest {
	public record Message(
		String message
	) {
	}

	public record ApplyChat(
		String conversationId
	) {

	}
}
