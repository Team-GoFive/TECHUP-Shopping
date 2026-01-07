package com.kt.ai.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.ai.VectorApi;
import com.kt.constant.FAQCategory;
import com.kt.constant.VectorType;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.FAQEntity;
import com.kt.exception.CustomException;
import com.kt.repository.faq.FAQRepository;
import com.kt.repository.vector.VectorStoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminFAQServiceImpl implements AdminFAQService {
	private final FAQRepository faqRepository;
	private final VectorApi vectorApi;
	private final VectorStoreRepository vectorStoreRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void create(
		String title,
		String content,
		FAQCategory category
	) throws Exception {
		var faq = faqRepository.save(
			FAQEntity.create(
				title,
				content,
				category
			)
		);

		var vector = vectorStoreRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		var fileId = vectorApi.uploadFile(
			vector.getStoreId(),
			objectMapper.writeValueAsBytes(faq)
		);

		faq.updateFileId(fileId);
	}

	@Override
	public void delete(UUID id) {
		var faq = faqRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FAQ));

		var vector = vectorStoreRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		vectorApi.delete(vector.getStoreId(), faq.getFileId());

		faqRepository.delete(faq);
	}
}
