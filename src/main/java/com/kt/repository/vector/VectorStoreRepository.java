package com.kt.repository.vector;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.VectorType;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.VectorStoreEntity;
import com.kt.exception.CustomException;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStoreEntity, UUID> {

	boolean existsByType(VectorType type);

	Optional<VectorStoreEntity> findByType(VectorType type);

	default VectorStoreEntity findByTypeOrThrow(VectorType type) {
		return findByType(type).orElseThrow(
			() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_TYPE)
		);
	}
}
