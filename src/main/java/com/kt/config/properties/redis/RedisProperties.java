package com.kt.config.properties.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
public record RedisProperties(
	String host,
	int port
) {
}
