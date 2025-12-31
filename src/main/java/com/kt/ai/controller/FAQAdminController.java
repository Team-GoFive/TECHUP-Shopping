package com.kt.ai.controller;

import static com.kt.common.api.ApiResult.*;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.dto.request.FAQRequest;
import com.kt.ai.service.AdminFAQService;
import com.kt.common.api.ApiResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/faq")
@RequiredArgsConstructor
public class FAQAdminController {

	private final AdminFAQService adminFaqService;

	@PostMapping
	public ResponseEntity<ApiResult<Void>> createFAQ(
		@RequestBody @Valid FAQRequest.Create request
	) throws Exception {
		adminFaqService.create(request.title(), request.content(), request.category());

		return empty();
	}

	@DeleteMapping("/{faqId}")
	public ResponseEntity<ApiResult<Void>> deleteFAQ(@PathVariable UUID faqId) {
		adminFaqService.delete(faqId);
		return empty();
	}

}
