package com.kt.ai.service;

import java.util.UUID;

public interface RAGService {
	String askFAQ(UUID userId, String question);

}
