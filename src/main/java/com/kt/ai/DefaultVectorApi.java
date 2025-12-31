package com.kt.ai;

import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.kt.ai.dto.request.OpenAIRequest;
import com.kt.ai.dto.response.OpenAIResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultVectorApi implements VectorApi {
	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;

	private String token() {
		return "Bearer " + openAIProperties.apiKey();
	}

	@Override
	public String create(String name, String description) {
		var response = openAIClient.create(
			token(),
			new OpenAIRequest.VectorCreate(name, description)
		);
		return response.id();
	}

	@Override
	public String uploadFile(String vectorStoreId, byte[] json) {
		var map = new LinkedMultiValueMap<String, Object>();

		var fileResource = new ByteArrayResource(
			json
		) {
			@Override
			public String getFilename() {
				return String.format("%s.json", UUID.randomUUID());
			}
		};

		map.add("purpose", "assistants");
		map.add("file", fileResource);

		var response = openAIClient.upload(
			token(),
			map
		);

		openAIClient.uploadVectorStore(
			vectorStoreId,
			token(),
			new OpenAIRequest.UploadFile(response.id())
		);

		return response.id();
	}

	@Override
	public void delete(String vectorStoreId, String fileId) {
		openAIClient.delete(
			vectorStoreId,
			fileId,
			token()
		);

		openAIClient.deleteFile(
			fileId,
			token()
		);
	}

	@Override
	public OpenAIResponse.Search search(String vectorStoreId, String question) {
		return openAIClient.search(
			vectorStoreId,
			token(),
			new OpenAIRequest.Search(question)
		);
	}
}