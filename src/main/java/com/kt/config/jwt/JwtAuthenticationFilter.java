package com.kt.config.jwt;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;
import com.kt.security.AuthenticationToken;

import com.kt.security.SecurityPath;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {

		boolean requiredPermission = Arrays.stream(SecurityPath.PUBLIC)
			.noneMatch(it -> matches(it, request.getRequestURI()));
		String token = jwtTokenProvider.resolve(request);
		try {
			if (requiredPermission && token != null) {
				jwtTokenProvider.validateToken(token);
				AuthenticationToken authenticationToken = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		} catch(ExpiredJwtException e) {
			reject(request, ErrorCode.AUTH_ACCESS_EXPIRED);
		} catch(JwtException | IllegalArgumentException e) {
			reject(request, ErrorCode.AUTH_INVALID);
		}
		filterChain.doFilter(request, response);
	}

	private boolean matches(String pattern, String path) {
		AntPathMatcher pathMatcher = new AntPathMatcher();
		return pathMatcher.match(pattern, path);
	}

	private void reject(HttpServletRequest request, ErrorCode error) {
		CustomException exception = new CustomException(error);
		request.setAttribute("exception", exception);
		throw exception;
	}

}
