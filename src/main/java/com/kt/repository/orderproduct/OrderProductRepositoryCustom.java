package com.kt.repository.orderproduct;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.response.OrderProductResponse;

public interface OrderProductRepositoryCustom {
	Page<OrderProductResponse.SearchReviewable> getReviewableOrderProductsByUserId(Pageable pageable, UUID userId);
}
