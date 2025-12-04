package com.kt.repository.product;

import java.util.UUID;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.ProductEntity;

import com.kt.repository.product.ProductRepositoryCustom;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>, ProductRepositoryCustom {

	default ProductEntity findByIdOrThrow(UUID productId) {
		return findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}
}
