package com.kt.chat.service;

import java.util.List;
import java.util.UUID;

import com.kt.chat.domain.dto.ChatRoomInfoResponse;

public interface AdminChatRoomService {
	void accept(UUID conversationId, UUID counselorId);

	List<ChatRoomInfoResponse> getAll();

	List<ChatRoomInfoResponse> getWaitingRooms();

	List<ChatRoomInfoResponse> getConnectedRooms();
}
