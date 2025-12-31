package com.kt.ai;

import com.kt.ai.dto.response.OpenAIResponse;

public interface VectorApi {
	/**
	 *
	 * @param name        벡터 스토어 이름
	 * @param description 벡터 스토어 설명
	 * @return 생성된 벡터 스토어 ID
	 */
	String create(String name, String description);

	String uploadFile(String vectorStoreId, byte[] json);

	void delete(String vectorStoreId, String fileId);

	OpenAIResponse.Search search(String vectorStoreId, String question);
}
