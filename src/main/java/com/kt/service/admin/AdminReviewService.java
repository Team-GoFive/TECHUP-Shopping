package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;

public interface AdminReviewService {
	void delete(String email, UUID reviewId);

	ReviewResponse.Search getReview(UUID orderProductId);

	Page<ReviewResponse.Search> getReviewsByAdmin(Pageable pageable, String keyword, ProductSearchType type);

	// TODO: 아래 두 서비스 메서드 컨트롤러 내 api 작성
	Page<ReviewResponse.Search> getReviewByProductId(UUID productId, Pageable pageable);

	Page<ReviewResponse.Search> getReviewsByUserId(Pageable pageable, UUID userId);

}
