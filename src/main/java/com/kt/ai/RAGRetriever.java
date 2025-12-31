package com.kt.ai;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kt.ai.dto.mapper.AIChatMapper;
import com.kt.ai.dto.response.OpenAIResponse;
import com.kt.constant.VectorType;
import com.kt.domain.entity.VectorStoreEntity;
import com.kt.repository.vector.VectorStoreRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RAGRetriever {

	private final VectorApi vectorApi;
	private final VectorStoreRepository vectorStoreRepository;

	public AIChatMapper.VectorSearchResult retrieve(String question) {
		VectorStoreEntity vectorStore = vectorStoreRepository.findByTypeOrThrow(VectorType.FAQ);
		OpenAIResponse.Search response = vectorApi.search(vectorStore.getStoreId(), question);

		var top = response.data().stream()
			.max(Comparator.comparingDouble(OpenAIResponse.SearchData::score))
			.orElse(null);

		if (top == null) {
			return new AIChatMapper.VectorSearchResult(0, null, null);
		}

		String joinedContent = top.content().stream()
			.map(OpenAIResponse.Content::text)
			.filter(t -> t != null && !t.isBlank())
			.collect(Collectors.joining("\n"));

		return new AIChatMapper.VectorSearchResult(top.score(), joinedContent, top.fileId());
	}
}
