package com.kt.ai.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BaseChatClient {

	private final ChatClient chatClient;

	public ChatClient.ChatClientRequestSpec prompt() {
		// TODO: advisor와 같이 공통 로직 추가 예정
		return chatClient.prompt();
	}

	public String ask(String userMessage) {
		return chatClient.prompt()
			.user(userMessage)
			.call()
			.content();
	}
}
