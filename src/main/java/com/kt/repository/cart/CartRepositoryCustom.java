package com.kt.repository.cart;

import java.util.Optional;
import java.util.UUID;

import com.kt.domain.entity.CartEntity;

public interface CartRepositoryCustom {
	Optional<CartEntity> findCartWithItems(UUID userId);
}
