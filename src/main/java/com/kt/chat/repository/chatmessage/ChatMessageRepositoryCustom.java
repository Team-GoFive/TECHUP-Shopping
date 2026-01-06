package com.kt.chat.repository.chatmessage;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.chat.domain.entity.ChatMessageEntity;

public interface ChatMessageRepositoryCustom {

	Page<ChatMessageEntity> findByConversationIdWithCursor(
		Pageable pageable,
		UUID conversationId,
		Instant cursor
	);
}
