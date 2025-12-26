package com.kt.ai;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.kt.ai.dto.OpenAIRequest;
import com.kt.ai.dto.OpenAIResponse;

@HttpExchange(url = "https://api.openai.com/v1", contentType = MediaType.APPLICATION_JSON_VALUE)
public interface OpenAIClient {
	@PostExchange("/vector_stores")
	OpenAIResponse.VectorCreate create(
		@RequestHeader("Authorization") String authorization,
		@RequestBody OpenAIRequest.VectorCreate request
	);

	@PostExchange(value = "/files", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
	OpenAIResponse.Upload upload(
		@RequestHeader("Authorization") String authorization,
		@RequestBody MultiValueMap<String, Object> request
	);

	@PostExchange(value = "/vector_stores/{vector_store_id}/files")
	void uploadVectorStore(
		@PathVariable("vector_store_id") String vectorStoreId,
		@RequestHeader("Authorization") String authorization,
		@RequestBody OpenAIRequest.UploadFile request
	);

	@DeleteExchange("/vector_stores/{vector_store_id}/files/{file_id}")
	void delete(
		@PathVariable("vector_store_id") String vectorStoreId,
		@PathVariable("file_id") String fileId,
		@RequestHeader("Authorization") String authorization
	);

	@DeleteExchange("/files/{file_id}")
	void deleteFile(
		@PathVariable("file_id") String fileId,
		@RequestHeader("Authorization") String authorization
	);

	@PostExchange("/vector_stores/{vector_store_id}/search")
	OpenAIResponse.Search search(
		@PathVariable("vector_store_id") String vectorStoreId,
		@RequestHeader("Authorization") String authorization,
		@RequestBody OpenAIRequest.Search request
	);

}