package com.kt.repository.cart;

import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.response.CartItemViewResponse;

public interface CartQueryRepository {

	List<CartItemViewResponse> findCartItems(UUID userId);

}
