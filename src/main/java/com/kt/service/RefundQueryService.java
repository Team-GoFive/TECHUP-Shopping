package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.kt.common.Paging;
import com.kt.domain.dto.response.RefundQueryResponse;

public interface RefundQueryService {
	Page<RefundQueryResponse> getMyRefunds(UUID userId, Paging paging);
	Page<RefundQueryResponse> getRequestedRefunds(UUID sellerId, Paging paging);
}
