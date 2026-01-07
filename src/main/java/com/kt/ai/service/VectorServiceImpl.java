package com.kt.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.ai.VectorApi;
import com.kt.constant.VectorType;
import com.kt.domain.entity.VectorStoreEntity;
import com.kt.repository.vector.VectorStoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VectorServiceImpl implements VectorService {

	private final VectorStoreRepository vectorStoreRepository;
	private final VectorApi vectorApi;

	// TODO: 배포 환경시에만 생성되도록 변경
	// @Override
	// @PostConstruct
	// void init() {
	// 	if (!vectorStoreRepository.existsByType(VectorType.FAQ)) {
	// 		var name = "FAQ 벡터 스토어";
	// 		var description = "자주 묻는 질문(FAQ) 데이터를 위한 벡터 스토어입니다.";
	//
	// 		var vectorStoreId = vectorApi.create(name, description);
	//
	// 		create(
	// 			vectorStoreId,
	// 			name,
	// 			description,
	// 			VectorType.FAQ
	// 		);
	// 	}
	// }

	@Override
	public void create(String storeId, String name, String description, VectorType type) {
		vectorStoreRepository.save(
			VectorStoreEntity.create(
				type,
				storeId,
				description,
				name
			)
		);
	}
}
