package com.kt.ai.service;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.ai.OpenAIClient;
import com.kt.ai.OpenAIProperties;
import com.kt.ai.client.BaseChatClient;
import com.kt.ai.client.FAQChatClient;
import com.kt.ai.dto.request.OpenAIRequest;
import com.kt.constant.VectorType;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.VectorStoreEntity;
import com.kt.exception.CustomException;
import com.kt.repository.vector.VectorStoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RAGServiceImpl implements RAGService {

	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;
	private final VectorStoreRepository vectorStoreRepository;
	private final BaseChatClient chatClient;

	public String askFAQ(String question, String conversationId) {
		VectorStoreEntity vectorStore = vectorStoreRepository.findByTypeOrThrow(VectorType.FAQ);

		var store = vectorStoreRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		var response = openAIClient.search(
			store.getStoreId(),
			"Bearer " + openAIProperties.apiKey(),
			new OpenAIRequest.Search(question)
		);

		if (response.data() == null || response.data().isEmpty()) {
			return "관련 FAQ를 찾지 못했어요. 😥\n"
				+ "조금 더 구체적으로 질문해주시겠어요?\n"
				+ "또는 1:1 상담 연결을 도와드릴 수 있어요!";
		}

		var context = response.data().stream()
			.flatMap(d -> d.content().stream())
			.map(c -> c.text())
			.limit(5)
			.collect(Collectors.joining("\n----\n"));

		System.out.println(context);
		return chatClient.prompt(conversationId)
			.system("""
				당신은 고객센터 FAQ AI입니다.
				아래 자료 안에서만 답변하세요.
				추측 금지, 모르면 "확인 후 안내드릴게요." 라고 답하세요.
				
				📌 참고 자료:
				%s
				""".formatted(context)
			)
			.user(question)
			.call()
			.content();
	}
}
