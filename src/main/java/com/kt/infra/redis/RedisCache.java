package com.kt.infra.redis;

import com.kt.constant.redis.RedisKey;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCache {

	private final RedisTemplate<String, Object> redisTemplate;

	public <T> void set(String key, T value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public <T> void set(String key, T value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
	}

	public <T> void set(RedisKey redisKey, Object keyParam, T value) {
		set(redisKey.key(keyParam), value, redisKey.getTtl());
	}

	public <T> T get(String key, Class<T> clazz) {
		Object value = redisTemplate.opsForValue().get(key);
		return value != null ? clazz.cast(value) : null;
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

}