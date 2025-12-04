package com.kt.config.properties.jwt;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private final String secret;
	private final Duration accessValidTime;
	private final Duration refreshValidTime;

	public SecretKey getSecret() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
}
