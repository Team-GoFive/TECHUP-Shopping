package com.kt.ai.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.ai.AIChatSessionStore;
import com.kt.ai.RAGRetriever;
import com.kt.ai.client.FAQChatClient;
import com.kt.ai.dto.mapper.AIChatMapper;
import com.kt.ai.dto.response.FAQResponse;
import com.kt.chat.event.HandoverEvent;
import com.kt.chat.event.HandoverPublisher;
import com.kt.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RAGServiceImpl implements RAGService {

	private static final double THRESHOLD = 0.5;
	private static final int MAX_FAIL_COUNT = 3;
	private final FAQChatClient chatClient;
	private final AIChatSessionStore chatSessionStore;
	private final RAGRetriever ragRetriever;
	private final HandoverPublisher handoverPublisher;

	@Override
	public FAQResponse.ChatBot askFAQ(UUID userId, String question) {

		UUID conversationId = chatSessionStore.getConversationId(userId)
			.orElseGet(() -> chatSessionStore.createConversationId(userId));

		AIChatMapper.VectorSearchResult rag = ragRetriever.retrieve(question);

		if (rag.score() < THRESHOLD) {
			int failCnt = chatSessionStore.increaseFail(userId);
			if (failCnt >= MAX_FAIL_COUNT) {
				handoverPublisher.publish(
					new HandoverEvent(userId, question, conversationId)
				);
				chatSessionStore.clear(userId);

				return new FAQResponse.ChatBot(
					"정확한 답변이 어려워 상담사에게 연결해드릴게요. 상담 연결 버튼을 누른 후 메시지를 보내시고 대기 부탁드려요.",
					conversationId,
					true
				);
			}

		}

		return new FAQResponse.ChatBot
			(chatClient.ask(question, rag, conversationId),
				conversationId,
				false);
	}
}
