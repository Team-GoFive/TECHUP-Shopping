package com.kt.chat.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.chat.domain.dto.ChatResponse;
import com.kt.chat.domain.entity.ChatMessageEntity;
import com.kt.chat.repository.chatmessage.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;

	public void save(UUID conversationId, UUID senderId, String senderRole, String message) {
		ChatMessageEntity msg = ChatMessageEntity.create(conversationId, senderId, senderRole, message);
		chatMessageRepository.save(msg);
	}

	public Page<ChatResponse.Search> getMessages(
		UUID conversationId,
		Instant cursor,
		Pageable pageable
	) {
		return chatMessageRepository.findByConversationIdWithCursor(
			pageable,
			conversationId,
			cursor
		).map(ChatResponse.Search::from);

	}
}