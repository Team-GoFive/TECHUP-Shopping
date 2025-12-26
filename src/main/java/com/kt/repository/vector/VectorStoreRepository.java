package com.kt.repository.vector;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.VectorType;
import com.kt.domain.entity.VectorStoreEntity;

@Repository
public interface VectorStoreRepository extends JpaRepository<VectorStoreEntity, UUID> {

	boolean existsByType(VectorType type);

	Optional<VectorStoreEntity> findByType(VectorType type);
}
