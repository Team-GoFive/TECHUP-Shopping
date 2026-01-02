package com.kt.chat.repository.chatroom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.chat.domain.entity.ChatRoomEntity;
import com.kt.constant.ChatStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {

	default ChatRoomEntity findByIdOrElseThrow(UUID conversationId) {
		return findById(conversationId)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
	}

	List<ChatRoomEntity> findByStatus(ChatStatus status);

}

