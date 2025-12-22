package com.kt.repository.refund;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.kt.domain.dto.response.RefundQueryResponse;

public interface RefundQueryRepository {

	Page<RefundQueryResponse> findRefundsByMember(
		UUID userId,
		Pageable pageable
	);

	Page<RefundQueryResponse> findRequestedRefundsBySeller(
		UUID sellerId,
		Pageable pageable
	);
}
