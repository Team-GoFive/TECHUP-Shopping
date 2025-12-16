package com.kt.config.jwt;

import com.kt.config.properties.jwt.JwtProperties;

import com.kt.constant.AccountRole;
import com.kt.constant.TokenType;

import com.kt.security.AuthenticationToken;

import com.kt.security.DefaultCurrentUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final JwtProperties jwtProperties;

	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String ROLE_CLAIM_KEY = "role";
	private static final String EMAIL_CLAIM_KEY = "email";

	public String create(UUID id, String email, AccountRole role, TokenType tokenType) {
		Date issuedAt = new Date();
		Duration validTime = tokenType == TokenType.ACCESS ?
			jwtProperties.getAccessValidTime() : jwtProperties.getRefreshValidTime();
		Date expireAt = new Date(issuedAt.getTime() + validTime.toMillis());
		return Jwts.builder()
			.subject(id.toString())
			.claim(ROLE_CLAIM_KEY, role.name())
			.claim(EMAIL_CLAIM_KEY, email)
			.issuedAt(issuedAt)
			.expiration(expireAt)
			.signWith(jwtProperties.getSecret())
			.compact();
	}

	public void validateToken(String token) throws JwtException {
		Jws<Claims> claims = Jwts.parser()
			.verifyWith(jwtProperties.getSecret())
			.build().parseSignedClaims(token);
	}

	public String resolve(HttpServletRequest request) {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (token != null && token.startsWith(TOKEN_PREFIX)) {
			return token.substring(TOKEN_PREFIX.length());
		}
		return token;
	}


	public AuthenticationToken getAuthentication(String token) {
		DefaultCurrentUser currentUser = toCurrentUser(token);
		return new AuthenticationToken(
			currentUser,
			currentUser.getAuthorities()
		);
	}

	public String getAccountId(String token) {
		return getClaims(token).getSubject();
	}

	private DefaultCurrentUser toCurrentUser(String token) {
		Claims claims = getClaims(token);
		UUID id = UUID.fromString(claims.getSubject());
		String email = claims.get(EMAIL_CLAIM_KEY, String.class);
		AccountRole role = AccountRole.valueOf(claims.get(ROLE_CLAIM_KEY, String.class));
		return new DefaultCurrentUser(id, email, role);
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(jwtProperties.getSecret())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

}
