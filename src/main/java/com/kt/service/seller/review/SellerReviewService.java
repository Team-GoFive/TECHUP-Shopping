package com.kt.service.seller.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.response.SellerReviewResponse;

public interface SellerReviewService {
	Page<SellerReviewResponse.Search> getAllReviews(Pageable pageable, UUID sellerId);

	Page<SellerReviewResponse.Search> getReviewsByProduct(Pageable pageable, UUID sellerId, UUID productId);
}
