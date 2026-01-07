package com.kt.chat.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.kt.chat.interceptor.JwtChannelInterceptor;

import lombok.RequiredArgsConstructor;

@EnableWebSocketMessageBroker
@Configuration
@RequiredArgsConstructor
public class WebSocketStompConfiguration implements WebSocketMessageBrokerConfigurer {

	private final JwtChannelInterceptor jwtChannelInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setAllowedOriginPatterns("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/pub");
		registry.enableSimpleBroker("/sub");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(jwtChannelInterceptor);
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> converters) {
		converters.add(new MappingJackson2MessageConverter());
		return false;
	}
}
