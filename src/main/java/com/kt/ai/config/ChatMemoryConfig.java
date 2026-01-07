package com.kt.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

	@Bean
	public ChatMemory chatMemory(ChatMemoryRepository repository) {
		return MessageWindowChatMemory.builder()
			.chatMemoryRepository(repository)
			.maxMessages(20)
			.build();
	}

	@Bean
	public ChatMemoryRepository chatMemoryRepository() {
		// TODO: 인메모리 -> 레디스/DB 전환
		return new InMemoryChatMemoryRepository();
	}
}
