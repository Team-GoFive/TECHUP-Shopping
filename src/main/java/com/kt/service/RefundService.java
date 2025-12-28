package com.kt.service;

import java.util.UUID;

public interface RefundService {
	void requestRefund(UUID userId, UUID orderProductId, String reason);

	void approveRefund(UUID sellerId, UUID refundId);

	void rejectRefund(UUID sellerId, UUID refundId, String reason);
}
