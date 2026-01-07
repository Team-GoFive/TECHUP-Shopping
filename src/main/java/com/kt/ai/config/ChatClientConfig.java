package com.kt.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder
			.defaultSystem("""
				You are an AI assistant operating within an e-commerce system.
				You must respond politely, factually, and avoid speculation.
				Always respond in Korean.
				Use natural and professional Korean appropriate for customer service.
				""")
			.build();
	}

}
