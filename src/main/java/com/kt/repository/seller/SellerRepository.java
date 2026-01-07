package com.kt.repository.seller;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.SellerEntity;
import com.kt.exception.CustomException;

public interface SellerRepository extends JpaRepository<SellerEntity, UUID> {
	default SellerEntity findByIdOrThrow(UUID id) {
		return findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.SELLER_NOT_FOUND)
		);
	}
}
