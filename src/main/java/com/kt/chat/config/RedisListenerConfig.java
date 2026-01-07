package com.kt.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.kt.chat.event.HandoverSubscriber;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

	@Bean
	public RedisMessageListenerContainer redisContainer(
		RedisConnectionFactory factory,
		HandoverSubscriber subscriber
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(factory);
		container.addMessageListener(subscriber, new PatternTopic("chat.handover"));
		return container;
	}
}

