package com.kt.repository.refund;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.RefundStatus;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.RefundHistoryEntity;

public interface RefundHistoryRepository
	extends JpaRepository<RefundHistoryEntity, UUID> {

	boolean existsByOrderProductAndStatus(
		OrderProductEntity orderProduct,
		RefundStatus status
	);
}

