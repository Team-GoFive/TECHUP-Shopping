package com.kt.service.refund;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.Paging;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.repository.refund.RefundQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundQueryServiceImpl implements RefundQueryService {
	private final RefundQueryRepository refundQueryRepository;

	@Override
	public Page<RefundQueryResponse> getMyRefunds(UUID userId, Paging paging) {
		return refundQueryRepository.findRefundsByMember(
			userId,
			paging.toPageable()
		);
	}

	@Override
	public Page<RefundQueryResponse> getRequestedRefunds(UUID sellerId, Paging paging) {
		return refundQueryRepository.findRefundsBySeller(
			sellerId,
			paging.toPageable()
		);
	}
}
