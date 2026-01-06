package com.kt.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class FAQRequest {
	@Schema(name = "UserFAQRequest")
	public record AskFAQ(
		@Schema(description = "유저 질문")
		String question
	) {

	}
}
