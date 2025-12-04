package com.kt.repository.orderproduct;

import java.util.List;
import java.util.UUID;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderProductEntity;

@Repository
public interface OrderProductRepository
	extends JpaRepository<OrderProductEntity, UUID>, OrderProductRepositoryCustom {

	List<OrderProductEntity> findAllByOrderId(UUID orderId);

	default OrderProductEntity findByIdOrThrow(UUID orderProductId) {
		return findById(orderProductId).orElseThrow(
			() -> new CustomException(ErrorCode.ORDER_PRODUCT_NOT_FOUND)
		);
	}

	List<OrderProductEntity> findAllByProductId(UUID productId);
}
