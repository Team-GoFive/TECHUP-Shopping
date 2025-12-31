package com.kt.repository.inventory;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.InventoryEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM inventory i WHERE i.productId = :productId")
	Optional<InventoryEntity> findByProductIdWithLock(UUID productId);

	default InventoryEntity findByProductIdWithLockOrThrow(UUID productId) {
		return findByProductIdWithLock(productId).orElseThrow(
			() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND)
		);
	}

	Optional<InventoryEntity> findByProductId(UUID productId);

	default InventoryEntity findByProductIdOrThrow(UUID productId) {
		return findByProductId(productId).orElseThrow(
			() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND)
		);
	}
}
