package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderStatus;
import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderProductRepository orderProductRepository;

	private static boolean hasReviewUpdatePermission(String email, ReviewEntity review) {
		UserEntity orderBy = review
			.getOrderProduct()
			.getOrder()
			.getOrderBy();

		if (orderBy.getRole() == UserRole.ADMIN) {
			return true;
		}
		if (email.equals(orderBy.getEmail())) {
			return true;
		}
		return false;
	}

	@Override
	public void create(UUID orderProductId, String content) {
		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);
		if (orderProduct.getOrder().getStatus() != OrderStatus.PURCHASE_CONFIRMED)
			throw new CustomException(ErrorCode.ORDER_NOT_CONFIRMED);

		ReviewEntity review = ReviewEntity.create(content);
		review.mapToOrderProduct(orderProduct);
		reviewRepository.save(review);
	}

	@Override
	public void update(
		String email,
		UUID reviewId,
		String content
	) {
		ReviewEntity review = reviewRepository.findByIdOrThrow(reviewId);
		if (!hasReviewUpdatePermission(email, review))
			throw new CustomException(ErrorCode.REVIEW_ACCESS_NOT_ALLOWED);
		review.update(content);
	}

	@Override
	public void delete(
		String email,
		UUID reviewId
	) {
		ReviewEntity review = reviewRepository.findByIdOrThrow(reviewId);
		if (!hasReviewUpdatePermission(email, review))
			throw new CustomException(ErrorCode.REVIEW_ACCESS_NOT_ALLOWED);
		review.delete();
	}

	@Override
	public ReviewResponse.Search getReview(UUID orderProductId) {
		ReviewEntity reviewEntity = reviewRepository.findByOrderProductIdOrThrow(orderProductId);
		return new ReviewResponse.Search(
			reviewEntity.getId(),
			reviewEntity.getContent()
		);
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
