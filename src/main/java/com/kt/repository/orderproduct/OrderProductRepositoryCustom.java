package com.kt.repository.orderproduct;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.SellerOrderResponse;
import com.kt.domain.entity.OrderProductEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderProductRepositoryCustom {
	Page<OrderProductResponse.SearchReviewable> getReviewableOrderProductsByUserId(Pageable pageable, UUID userId);

	List<OrderProductEntity> findWithProductByOrderId(UUID orderId);

	Page<SellerOrderResponse.Search> search(Pageable pageable, UUID orderProductId, OrderProductStatus status,
		UUID sellerId);
}