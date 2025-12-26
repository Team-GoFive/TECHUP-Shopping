package com.kt.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.openai")
public record OpenAIProperties(
	String apiKey
) {
}