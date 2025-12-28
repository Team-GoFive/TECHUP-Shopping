package com.kt.repository.product;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.ProductEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, ProductRepositoryCustom {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM product p WHERE p.id = :productId")
	Optional<ProductEntity> findByIdWithLock(@Param("productId") UUID productId);

	default ProductEntity findByIdOrThrow(UUID productId) {
		return findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}

	default ProductEntity findByIdWithLockOrThrow(UUID productId) {
		return findByIdWithLock(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}
}
