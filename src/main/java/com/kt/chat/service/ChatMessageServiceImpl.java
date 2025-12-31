package com.kt.chat.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.chat.domain.entity.ChatMessageEntity;
import com.kt.chat.repository.chatmessage.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

	private final ChatMessageRepository messageRepository;

	public void save(UUID conversationId, UUID senderId, String senderRole, String message) {
		ChatMessageEntity msg = ChatMessageEntity.create(conversationId, senderId, senderRole, message);
		messageRepository.save(msg);
	}
}