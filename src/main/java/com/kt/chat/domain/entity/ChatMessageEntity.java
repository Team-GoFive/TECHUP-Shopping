package com.kt.chat.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.domain.entity.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class ChatMessageEntity extends BaseTimeEntity {

	@Id
	@UuidGenerator
	@Column(name = "id", nullable = false, updatable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private UUID id;

	private UUID conversationId;

	private UUID senderId;
	private String senderRole;

	@Column(columnDefinition = "TEXT")
	private String message;

	private ChatMessageEntity(
		UUID conversationId,
		UUID senderId,
		String senderRole,
		String message
	) {
		this.conversationId = conversationId;
		this.senderId = senderId;
		this.senderRole = senderRole;
		this.message = message;
	}

	public static ChatMessageEntity create(
		UUID conversationId,
		UUID senderId,
		String senderRole,
		String message
	) {
		return new ChatMessageEntity(conversationId, senderId, senderRole, message);
	}
}
