package com.kt.chat.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.chat.domain.entity.ChatRoomEntity;
import com.kt.chat.repository.chatroom.ChatRoomRepository;
import com.kt.constant.ChatStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	@Override
	public void createOrWaiting(UUID conversationId, UUID userId) {
		if (!chatRoomRepository.existsById(conversationId)) {
			ChatRoomEntity chatRoom = ChatRoomEntity.create(conversationId, userId, ChatStatus.WAITING);
			chatRoomRepository.save(chatRoom);
		}
	}

}
