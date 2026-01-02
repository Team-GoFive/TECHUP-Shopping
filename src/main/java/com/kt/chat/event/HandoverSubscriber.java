package com.kt.chat.event;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class HandoverSubscriber implements MessageListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper;

	@Override
	@SneakyThrows
	public void onMessage(Message message, byte[] pattern) {
		byte[] body = message.getBody();
		HandoverEvent event = objectMapper.readValue(body, HandoverEvent.class);

		messagingTemplate.convertAndSend("/sub/admin/handover", event);
	}
}
