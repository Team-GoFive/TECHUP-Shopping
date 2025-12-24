package com.kt.ai.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BaseChatClient {

	private final ChatClient chatClient;

	public String ask(String userMessage) {
		return chatClient.prompt()
			.user(userMessage)
			.call()
			.content();
	}
}
