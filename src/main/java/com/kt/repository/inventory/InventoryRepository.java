package com.kt.repository.inventory;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.InventoryEntity;
import com.kt.exception.CustomException;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {

	Optional<InventoryEntity> findByProductId(UUID productId);

	default InventoryEntity findByProductIdOrThrow(UUID productId) {
		return findByProductId(productId).orElseThrow(
			() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND)
		);
	}
}
