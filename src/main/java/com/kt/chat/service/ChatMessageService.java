package com.kt.chat.service;

import java.util.UUID;

public interface ChatMessageService {

	void save(UUID conversationId, UUID senderId, String senderRole, String message);
}
