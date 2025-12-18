package com.kt.service.seller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.SellerReviewResponse;

public interface SellerReviewService {
	Page<SellerReviewResponse.search> getAllReviews(Pageable pageable, UUID sellerId);

	Page<SellerReviewResponse.search> getReviewsByProduct(Pageable pageable, UUID sellerId, UUID productId);
}
