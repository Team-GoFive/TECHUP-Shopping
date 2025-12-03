package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;

public interface ReviewService {
	void create(String email, UUID orderProductId, String content);

	void update(String email, UUID reviewId, String content);

	void delete(String email, UUID reviewId);

	ReviewResponse.Search getReview(UUID orderProductId);

	Page<ReviewResponse.Search> getReviewByProductId(UUID productId, Pageable pageable);

	Page<ReviewResponse.Search> getReviewsByUserId(Pageable pageable, UUID userId);

	Page<ReviewResponse.Search> getReviewsByAdmin(Pageable pageable, String keyword, ProductSearchType type);
}