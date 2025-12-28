package com.kt.repository.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CartItemEntity;
import com.kt.exception.CustomException;

public interface CartItemRepository extends JpaRepository<CartItemEntity, UUID> {
	default CartItemEntity findByIdOrThrow(UUID cartItemId) {
		return findById(cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));
	}
}
