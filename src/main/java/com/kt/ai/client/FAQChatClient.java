package com.kt.ai.client;

import org.springframework.stereotype.Component;

import com.kt.ai.dto.mapper.AIChatMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FAQChatClient {

	private final BaseChatClient baseChatClient;

	public String ask(String question, AIChatMapper.VectorSearchResult rag, String conversationId) {
		return baseChatClient.prompt(conversationId)
			.system("""
				당신은 고객센터 FAQ AI입니다.
				아래 자료 안에서만 답변하세요.
				추측 금지, 모르면 "확인 후 안내드릴게요." 라고 답하세요.
				
				📌 참고 자료:
				%s
				""".formatted(rag)
			)
			.user(question)
			.call()
			.content();
	}
}
