package com.kt.ai.client;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FAQChatClient {

	private final BaseChatClient baseChatClient;

	public String ask(String userMessage, String conversationId) {
		return baseChatClient.prompt(conversationId)
			.user(userMessage)
			.call()
			.content();
	}
}
