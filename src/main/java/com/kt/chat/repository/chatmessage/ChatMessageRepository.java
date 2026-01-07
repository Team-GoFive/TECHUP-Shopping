package com.kt.chat.repository.chatmessage;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.chat.domain.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID>, ChatMessageRepositoryCustom {

}
