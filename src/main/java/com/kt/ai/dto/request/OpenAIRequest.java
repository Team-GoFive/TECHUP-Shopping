package com.kt.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenAIRequest {
	public record VectorCreate(
		String name,
		String description
	) {
	}

	public record UploadFile(
		@JsonProperty("file_id")
		String id
	) {
	}

	public record Search(
		String query
	) {
	}
}
