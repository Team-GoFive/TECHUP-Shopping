package com.kt.ai;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kt.constant.redis.RedisKey;
import com.kt.infra.redis.RedisCache;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AIChatSessionStore {
	private final RedisCache redisCache;

	public Optional<UUID> getConversationId(UUID userId) {
		String key = RedisKey.AI_CHAT_SESSION.key(userId);
		String conversationId = redisCache.get(key, String.class);

		return Optional.ofNullable(conversationId).map(UUID::fromString);
	}

	public UUID createConversationId(UUID userId) {
		UUID conversationId = UUID.randomUUID();
		redisCache.set(RedisKey.AI_CHAT_SESSION, userId, conversationId.toString());
		return conversationId;
	}

	public int increaseFail(UUID userId) {
		String key = RedisKey.AI_CHAT_FAIL.key(userId);
		Integer count = redisCache.get(key, Integer.class);

		if (count == null)
			count = 0;

		count++;

		redisCache.set(RedisKey.AI_CHAT_FAIL, userId, count);
		return count;
	}

	public void clear(UUID userId) {
		redisCache.delete(RedisKey.AI_CHAT_SESSION.key(userId));
	}
}
