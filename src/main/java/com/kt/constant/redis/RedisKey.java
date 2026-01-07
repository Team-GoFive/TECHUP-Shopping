package com.kt.constant.redis;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum RedisKey {
	SIGNUP_CODE("signup:code:", Duration.ofMinutes(3)),
	SIGNUP_VERIFIED("signup:verified:", Duration.ofMinutes(5)),
	REFRESH_TOKEN("refresh-token:", Duration.ofHours(24)),
	AI_CHAT_SESSION("ai-chat-session:%s", Duration.ofHours(24)),
	AI_CHAT_FAIL("ai-chat-fail:%s", Duration.ofHours(24)),
	;

	private final String prefix;

	private final Duration ttl;

	RedisKey(String prefix, Duration ttl) {
		this.prefix = prefix;
		this.ttl = ttl;
	}

	public String key(Object val) {
		return prefix + val;
	}

}