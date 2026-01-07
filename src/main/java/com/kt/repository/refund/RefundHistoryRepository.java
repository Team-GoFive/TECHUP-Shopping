package com.kt.repository.refund;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.RefundStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.RefundHistoryEntity;
import com.kt.exception.CustomException;

public interface RefundHistoryRepository
	extends JpaRepository<RefundHistoryEntity, UUID> {

	boolean existsByOrderProductAndStatus(
		OrderProductEntity orderProduct,
		RefundStatus status
	);

	default RefundHistoryEntity findByIdOrThrow(UUID refundId) {
		return findById(refundId)
			.orElseThrow(() ->
				new CustomException(ErrorCode.REFUND_NOT_ALLOWED)
			);
	}
}

