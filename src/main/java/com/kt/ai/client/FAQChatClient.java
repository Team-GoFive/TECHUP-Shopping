package com.kt.ai.client;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class FAQChatClient {

	// TODO: userMessage 탬플릿 구체화

	private BaseChatClient baseChatClient;

	String ask(String userMessage) {
		return baseChatClient.prompt()
			.user(userMessage)
			.call()
			.content();
	}

}
