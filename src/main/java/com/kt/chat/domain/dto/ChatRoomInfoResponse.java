package com.kt.chat.domain.dto;

import java.time.Instant;
import java.util.UUID;

import com.kt.chat.domain.entity.ChatRoomEntity;
import com.kt.constant.ChatStatus;

public record ChatRoomInfoResponse(
	UUID conversationId,
	UUID userId,
	UUID counselorId,
	ChatStatus status,
	Instant updatedAt
) {
	public static ChatRoomInfoResponse from(ChatRoomEntity chatRoomEntity) {
		return new ChatRoomInfoResponse(
			chatRoomEntity.getConversationId(),
			chatRoomEntity.getUserId(),
			chatRoomEntity.getCounselorId(),
			chatRoomEntity.getStatus(),
			chatRoomEntity.getUpdatedAt()
		);
	}
}
