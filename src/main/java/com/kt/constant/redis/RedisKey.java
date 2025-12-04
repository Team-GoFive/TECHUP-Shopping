package com.kt.constant.redis;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum RedisKey {
	SIGNUP_CODE("signup:code:", Duration.ofMinutes(3)),
	SIGNUP_VERIFIED("signup:verified:", Duration.ofMinutes(5));

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