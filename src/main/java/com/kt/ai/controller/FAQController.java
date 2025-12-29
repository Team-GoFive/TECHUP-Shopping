package com.kt.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.client.FAQChatClient;
import com.kt.ai.service.RAGService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class FAQController {

	private final RAGService ragService;

	@PostMapping("/faq")
	public String askFAQ(
		@RequestParam String userId,
		@RequestBody String question
	) {

		// TODO: 실제 채팅방 ID로 변경 필요
		String conversationId = "faq:" + userId;

		return ragService.askFAQ(question, conversationId);
	}

}
