package com.kt.ai.dto.request;

import com.kt.constant.FAQCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public class FAQRequest {

	@Schema(name = "FAQCreateRequest")
	public record Create(
		String title,
		String content,
		FAQCategory category
	) {

	}
}
