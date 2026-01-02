package com.kt.chat.event;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HandoverPublisher {

	private static final String TOPIC = "chat.handover";

	private final RedisTemplate<String, Object> redisTemplate;

	public void publish(HandoverEvent event) {
		redisTemplate.convertAndSend(TOPIC, event);
	}
}