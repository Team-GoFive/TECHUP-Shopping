package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;

public interface ReviewService {
	void create(UUID orderProductId, String content);

	void update(UUID reviewId, String content);

	void delete(UUID reviewId);

	ReviewResponse.Search getReview(UUID orderProductId);

	List<ReviewResponse.Search> getReviewByProductId(UUID productId);

	Page<ReviewResponse.Search> getReviewByProductId(UUID productId, Pageable pageable);

	Page<ReviewResponse.Search> getReviewsByUserId(Pageable pageable, UUID userId);

	Page<ReviewResponse.Search> getReviewsByAdmin(Pageable pageable, String keyword, ProductSearchType type);
}