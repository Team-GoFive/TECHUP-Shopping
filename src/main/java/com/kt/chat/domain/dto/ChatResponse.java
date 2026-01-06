package com.kt.chat.domain.dto;

import java.time.Instant;
import java.util.UUID;

import com.kt.chat.domain.entity.ChatMessageEntity;
import com.querydsl.core.annotations.QueryProjection;

public class ChatResponse {
	public record Message(
		UUID senderId,
		String senderRole,
		String message
	) {

	}

	public record Search(
		UUID id,
		UUID senderId,
		String senderRole,
		String message,
		Instant createdAt
	) {
		@QueryProjection
		public Search {

		}

		public static Search from(ChatMessageEntity chatMessageEntity) {
			return new Search(
				chatMessageEntity.getId(),
				chatMessageEntity.getSenderId(),
				chatMessageEntity.getSenderRole(),
				chatMessageEntity.getMessage(),
				chatMessageEntity.getCreatedAt()
			);
		}

	}

}
