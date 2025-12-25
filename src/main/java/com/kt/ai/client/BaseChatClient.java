package com.kt.ai.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BaseChatClient {

	private final ChatClient chatClient;
	private final ChatMemory chatMemory;

	public ChatClient.ChatClientRequestSpec prompt(String conversationId) {
		// TODO: RAG 로직 추가
		return chatClient.prompt()
			.advisors(
				MessageChatMemoryAdvisor
					.builder(chatMemory)
					.conversationId(conversationId)
					.build()
			);
	}

	public String ask(String userMessage) {
		return chatClient.prompt()
			.user(userMessage)
			.call()
			.content();
	}
}
