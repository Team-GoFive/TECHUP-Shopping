package com.kt.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CategoryEntity;
import com.kt.exception.CustomException;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

	Optional<CategoryEntity> findByName(String name);

	default CategoryEntity findByIdOrThrow(UUID id) {
		return findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);
	}
}
