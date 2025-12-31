package com.kt.ai.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.service.RAGService;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class FAQController {

	private final RAGService ragService;

	@PostMapping("/faq")
	public String askFAQ(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody String question
	) {

		return ragService.askFAQ(defaultCurrentUser.getId(), question);
	}

}
