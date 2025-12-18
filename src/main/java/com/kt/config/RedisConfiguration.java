package com.kt.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kt.common.profile.DevProfile;
import com.kt.common.profile.LocalProfile;
import com.kt.common.profile.ProdProfile;
import com.kt.common.profile.TestProfile;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

	private final RedisProperties redisProperties;

	@Bean
	@DevProfile
	@ProdProfile
	public RedissonClient redissonClient() {
		var config = new Config();
		var host = redisProperties.getCluster().getNodes().getFirst();
		var uri = String.format("rediss://%s", host);

		config
			.useClusterServers()
			.addNodeAddress(uri);

		return Redisson.create(config);
	}

	@Bean
	@LocalProfile
	@TestProfile
	public RedissonClient localRedissonClient() {
		var config = new Config();
		var host = redisProperties.getCluster().getNodes().getFirst();
		var uri = String.format("redis://%s", host);

		config
			.useSingleServer().setAddress(uri);
		return Redisson.create(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);

		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer serializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);

		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		template.afterPropertiesSet();
		return template;
	}
}
