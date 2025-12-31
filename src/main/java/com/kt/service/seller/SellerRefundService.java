package com.kt.service.seller;

import java.util.UUID;

public interface SellerRefundService {
	void approveRefund(UUID sellerId, UUID refundId);

	void rejectRefund(UUID sellerId, UUID refundId, String reason);
}
