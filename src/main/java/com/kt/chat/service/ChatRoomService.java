package com.kt.chat.service;

import java.util.UUID;

public interface ChatRoomService {

	void createOrWaiting(UUID sessionId, UUID userId);
	
}
