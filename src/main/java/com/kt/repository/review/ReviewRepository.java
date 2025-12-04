package com.kt.repository.review;

import java.util.Optional;
import java.util.UUID;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.ReviewEntity;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID>, ReviewRepositoryCustom{
	default ReviewEntity findByIdOrThrow(UUID reviewId) {
		return findById(reviewId).orElseThrow(
			() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND)
		);
	}

	Optional<ReviewEntity> findByOrderProductId(UUID orderProductId);

	default ReviewEntity findByOrderProductIdOrThrow(UUID orderProductId) {
		return findByOrderProductId(orderProductId).orElseThrow(
			() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND)
		);
	}
}
