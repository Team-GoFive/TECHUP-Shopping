package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.ReviewEntity;
import com.kt.repository.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

	private final ReviewRepository reviewRepository;

	@Override
	public ReviewResponse.Search getReview(UUID orderProductId) {
		ReviewEntity reviewEntity = reviewRepository.findByOrderProductIdOrThrow(orderProductId);
		return new ReviewResponse.Search(
			reviewEntity.getId(),
			reviewEntity.getContent()
		);
	}

	@Override
	public void delete(UUID reviewId) {
		ReviewEntity review = reviewRepository.findByIdOrThrow(reviewId);
		review.delete();
	}

	@Override
	public Page<ReviewResponse.Search> getReviewByProductId(
		UUID productId,
		Pageable pageable
	) {
		return reviewRepository.searchReviewsByProductId(pageable, productId);
	}

	@Override
	public Page<ReviewResponse.Search> getReviewsByAdmin(
		Pageable pageable,
		String keyword,
		ProductSearchType type
	) {
		return reviewRepository.searchReviews(pageable, keyword, type);
	}

	@Override
	public Page<ReviewResponse.Search> getReviewsByUserId(Pageable pageable, UUID userId) {
		return reviewRepository.searchReviewsByUserId(pageable, userId);
	}
}
