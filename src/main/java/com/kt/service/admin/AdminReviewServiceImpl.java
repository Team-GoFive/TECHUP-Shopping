package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReviewServiceImpl implements AdminReviewService {

	private final ReviewRepository reviewRepository;
	private final AccountRepository accountRepository;

	@Override
	public ReviewResponse.Search getReview(UUID orderProductId) {
		ReviewEntity reviewEntity = reviewRepository.findByOrderProductIdOrThrow(orderProductId);
		return new ReviewResponse.Search(
			reviewEntity.getId(),
			reviewEntity.getContent()
		);
	}

	@Override
	public void delete(
		String email,
		UUID reviewId
	) {
		ReviewEntity review = reviewRepository.findByIdOrThrow(reviewId);
		if (!hasReviewAccessPermission(email, review))
			throw new CustomException(ErrorCode.REVIEW_ACCESS_NOT_ALLOWED);
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

	private boolean hasReviewAccessPermission(String email, ReviewEntity review) {
		AbstractAccountEntity reviewEditor = accountRepository.findByEmailOrThrow(email);

		UserEntity reviewOwner = review
			.getOrderProduct()
			.getOrder()
			.getOrderBy();

		return reviewEditor.getEmail().equals(reviewOwner.getEmail());
	}
}
