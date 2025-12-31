package com.kt.domain.entity;

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
	@Column(length = 200)
	private String sessionId;

	private UUID userId;
	private UUID counselorId;

	@Enumerated(EnumType.STRING)
	private ChatStatus status;

}
