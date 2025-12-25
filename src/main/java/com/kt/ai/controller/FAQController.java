package com.kt.ai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.client.FAQChatClient;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class FAQController {

	private final FAQChatClient faqChatClient;

	@GetMapping("/faq")
	public String ask(
		@RequestParam String userId,
		@RequestParam String message
	) {
		// TODO: 실제 채팅방 ID로 변경 필요
		String conversationId = "faq:" + userId;

		return faqChatClient.ask(message, conversationId);
	}
}
