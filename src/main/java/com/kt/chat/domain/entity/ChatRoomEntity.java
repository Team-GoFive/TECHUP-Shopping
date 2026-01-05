package com.kt.chat.domain.entity;

import java.util.UUID;

import com.kt.constant.ChatStatus;
import com.kt.domain.entity.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomEntity extends BaseTimeEntity {

	@Id
	private UUID conversationId;

	private UUID userId;
	private UUID counselorId;

	@Enumerated(EnumType.STRING)
	private ChatStatus status;

	private ChatRoomEntity(UUID conversationId, UUID userId, ChatStatus status) {
		this.conversationId = conversationId;
		this.userId = userId;
		this.counselorId = null;
		this.status = status;
	}

	public static ChatRoomEntity create(UUID conversationId, UUID userId, ChatStatus status) {
		return new ChatRoomEntity(conversationId, userId, status);
	}

	public void acceptCounselor(UUID counselorId) {
		this.counselorId = counselorId;
		this.status = ChatStatus.CONNECTED;
	}

}
