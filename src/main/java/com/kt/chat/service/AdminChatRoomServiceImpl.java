package com.kt.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.chat.domain.dto.ChatRoomInfoResponse;
import com.kt.chat.domain.entity.ChatRoomEntity;
import com.kt.chat.repository.chatroom.ChatRoomRepository;
import com.kt.constant.ChatStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminChatRoomServiceImpl implements AdminChatRoomService {

	private final ChatRoomRepository chatRoomRepository;

	@Override
	public void accept(UUID conversationId, UUID counselorId) {
		ChatRoomEntity chatRoomEntity = chatRoomRepository.findByConversationIdOrElseThrow(conversationId);
		chatRoomEntity.acceptCounselor(counselorId);
	}

	@Override
	public List<ChatRoomInfoResponse> getAll() {
		return chatRoomRepository.findAll().stream()
			.map(ChatRoomInfoResponse::from)
			.toList();
	}

	@Override
	public List<ChatRoomInfoResponse> getWaitingRooms() {
		return chatRoomRepository.findByStatus(ChatStatus.WAITING).stream()
			.map(ChatRoomInfoResponse::from)
			.toList();
	}

	@Override
	public List<ChatRoomInfoResponse> getConnectedRooms() {
		return chatRoomRepository.findByStatus(ChatStatus.CONNECTED).stream()
			.map(ChatRoomInfoResponse::from)
			.toList();
	}
}
