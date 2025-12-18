package com.kt.repository.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.dto.response.SellerReviewResponse;

public interface ReviewRepositoryCustom {
	Page<ReviewResponse.Search> searchReviews(Pageable pageable, String keyword, ProductSearchType type);

	Page<ReviewResponse.Search> searchReviewsByUserId(Pageable pageable, UUID userId);

	Page<ReviewResponse.Search> searchReviewsByProductId(Pageable pageable, UUID productId);

	Page<SellerReviewResponse.search> searchReviewsForSeller(Pageable pageable, UUID sellerId, UUID productId);
}
