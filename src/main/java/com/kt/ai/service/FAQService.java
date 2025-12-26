package com.kt.ai.service;

import java.util.UUID;

import com.kt.constant.FAQCategory;

public interface FAQService {

	void create(String title, String Content, FAQCategory category) throws Exception;

	void delete(UUID id);
}
