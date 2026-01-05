package com.kt.ai.dto.response;

import java.util.UUID;

public class FAQResponse {
	public record ChatBot(
		String answer,
		UUID conversationId,
		Boolean handoverTriggered
	) {

	}

}
