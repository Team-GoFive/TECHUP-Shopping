package com.kt.ai.service;

import com.kt.constant.VectorType;

public interface VectorService {

	// TODO: 배포 환경시에만 생성되도록 변경
	// void init()

	void create(String storeId, String name, String description, VectorType type);
}
