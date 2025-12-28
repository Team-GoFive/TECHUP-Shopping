package com.kt.repository.cart;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CartEntity;
import com.kt.exception.CustomException;

public interface CartRepository extends JpaRepository<CartEntity, UUID> {
	Optional<CartEntity> findByUserId(UUID userId);

	default CartEntity findByUserIdOrThrow(UUID userId) {
		return findByUserId(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
	}
}
