package com.kt.chat.service;

import java.util.UUID;

public interface ChatMessageService {

	void save(String sessionId, UUID senderId, String senderRole, String message);
}
