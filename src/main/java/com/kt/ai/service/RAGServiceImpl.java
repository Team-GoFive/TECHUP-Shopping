package com.kt.ai.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.ai.AIChatSessionStore;
import com.kt.ai.RAGRetriever;
import com.kt.ai.client.FAQChatClient;
import com.kt.ai.dto.mapper.AIChatMapper;

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

	@Override
	public String askFAQ(UUID userId, String question) {

		String conversationId = chatSessionStore.getOrCreate(userId);
		AIChatMapper.VectorSearchResult rag = ragRetriever.retrieve(question);

		if (rag.score() < THRESHOLD) {
			int failCnt = chatSessionStore.increaseFail(userId);
			if (failCnt >= MAX_FAIL_COUNT) {
				chatSessionStore.clear(userId);
				return "정확한 답변이 어려워 상담사에게 연결해드릴게요.";
			}

		}

		return chatClient.ask(question, rag, conversationId);
	}
}
