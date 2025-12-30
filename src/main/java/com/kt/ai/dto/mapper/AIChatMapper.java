package com.kt.ai.dto.mapper;

public class AIChatMapper {
	public record VectorSearchResult(
		double score,
		String content,
		String fileId
	) {

	}

}
