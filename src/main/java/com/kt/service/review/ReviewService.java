package com.kt.service.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;

public interface ReviewService {
	void create(UUID userId, UUID orderProductId, String content);

	void update(UUID userId, UUID reviewId, String content);

	void delete(UUID userId, UUID reviewId);

	ReviewResponse.Search getReview(UUID orderProductId);

	Page<ReviewResponse.Search> getReviewByProductId(UUID productId, Pageable pageable);

	Page<ReviewResponse.Search> getReviewsByUserId(Pageable pageable, UUID userId);

	Page<ReviewResponse.Search> getReviewsByAdmin(Pageable pageable, String keyword, ProductSearchType type);
}