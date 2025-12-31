package com.kt.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.domain.entity.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

	private String sessionId;

	private UUID senderId;
	private String senderRole;

	@Column(columnDefinition = "TEXT")
	private String message;

}
