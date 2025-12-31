package com.kt.chat.repository.chatroom;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.chat.domain.entity.ChatRoomEntity;
import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {

	Optional<ChatRoomEntity> findBySessionId(UUID sessionId);

	default ChatRoomEntity findBySessionIdOrElseThrow(UUID sessionId) {
		return findBySessionId(sessionId)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
	}
}

