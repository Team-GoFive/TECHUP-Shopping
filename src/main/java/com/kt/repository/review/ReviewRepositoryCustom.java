package com.kt.repository.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;

public interface ReviewRepositoryCustom {
	Page<ReviewResponse.Search> searchReviews(Pageable pageable, String keyword, ProductSearchType type);

	Page<ReviewResponse.Search> searchReviewsByUserId(Pageable pageable, UUID userId);

	Page<ReviewResponse.Search> searchReviewsByProductId(Pageable pageable, UUID productId);
}
