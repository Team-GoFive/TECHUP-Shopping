package com.kt.chat.service;

import java.util.UUID;

public interface AdminChatRoomService {
	void accept(UUID sessionId, UUID counselorId);

}
