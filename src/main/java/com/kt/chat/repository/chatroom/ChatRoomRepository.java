package com.kt.chat.repository.chatroom;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.chat.domain.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {
}
