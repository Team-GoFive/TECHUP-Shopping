package com.kt.chat.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.chat.domain.dto.ChatResponse;

public interface ChatMessageService {

	void save(UUID conversationId, UUID senderId, String senderRole, String message);

	Page<ChatResponse.Search> getMessages(
		UUID conversationId,
		Instant cursor,
		Pageable pageable
	);
}
