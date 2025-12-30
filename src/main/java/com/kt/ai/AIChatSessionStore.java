package com.kt.ai;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.constant.redis.RedisKey;
import com.kt.infra.redis.RedisCache;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AIChatSessionStore {
	private final RedisCache redisCache;

	public String getOrCreate(UUID userId) {
		String key = RedisKey.AI_CHAT_SESSION.key(userId);
		String conversationId = redisCache.get(key, String.class);

		if (conversationId == null) {
			redisCache.set(RedisKey.AI_CHAT_SESSION, userId, UUID.randomUUID().toString());
		}

		return conversationId;
	}

	public void clear(UUID userId) {
		redisCache.delete(RedisKey.AI_CHAT_SESSION.key(userId));
	}
}
