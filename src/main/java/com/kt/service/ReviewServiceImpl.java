package com.kt.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReviewEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

// TODO: 본인 확인 ID로 변경
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderProductRepository orderProductRepository;
	private final AccountRepository accountRepository;
	private final OrderRepository orderRepository;

	@Override
	public void create(
		String email,
		UUID orderProductId,
		String content
	) {
		if (!isOrderProductOwnedByUser(email, orderProductId))
			throw new CustomException(ErrorCode.REVIEW_ACCESS_NOT_ALLOWED);

		if (reviewRepository.findByOrderProductId(orderProductId).isPresent())
			throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);

		OrderProductEntity orderProduct = orderProductRepository.findByIdOrThrow(orderProductId);
		if (orderProduct.getStatus() != OrderProductStatus.PURCHASE_CONFIRMED)
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
		if (!hasReviewAccessPermission(email, review))
			throw new CustomException(ErrorCode.REVIEW_ACCESS_NOT_ALLOWED);
		review.update(content);
	}

	// TODO: 현재 본인만 리뷰 삭제 가능, 어드민 리뷰 삭제 검토
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

	// TODO: for admin
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

	private boolean isOrderProductOwnedByUser(String email, UUID orderProductId) {
		AbstractAccountEntity user = accountRepository.findByEmailOrThrow(email);
		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(user.getId());

		return orders
			.stream()
			.flatMap(order -> order.getOrderProducts().stream())
			.anyMatch(orderProduct -> orderProduct.getId().equals(orderProductId));
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
