package com.kt.repository.order;

import java.util.Optional;
import java.util.UUID;

import com.kt.domain.entity.OrderEntity;

public interface OrderRepositoryCustom {
	Optional<OrderEntity> findDetailWithProducts(UUID orderId);

}
