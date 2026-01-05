package com.kt.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.dto.request.FAQRequest;
import com.kt.ai.dto.response.FAQResponse;
import com.kt.ai.service.RAGService;
import com.kt.common.api.ApiResult;
import com.kt.security.DefaultCurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class FAQController implements FAQSwaggerSupporter {

	private final RAGService ragService;

	@PostMapping("/faq")
	public ResponseEntity<ApiResult<FAQResponse.ChatBot>> askFAQ(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody FAQRequest.AskFAQ request
	) {
		FAQResponse.ChatBot answer = ragService.askFAQ(defaultCurrentUser.getId(), request.question());

		return ApiResult.wrap(answer);
	}

}
