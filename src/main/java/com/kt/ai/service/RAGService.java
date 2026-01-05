package com.kt.ai.service;

import java.util.UUID;

import com.kt.ai.dto.response.FAQResponse;

public interface RAGService {
	FAQResponse.ChatBot askFAQ(UUID userId, String question);

}
