package com.kt.chat.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.kt.config.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor =
			MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (accessor == null) {
			return message;
		}

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {

			String authHeader = accessor.getFirstNativeHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				Authentication authentication =
					jwtTokenProvider.getAuthentication(token);

				accessor.setUser(authentication);
			}
		}

		return message;
	}
}
