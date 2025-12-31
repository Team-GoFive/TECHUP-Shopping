package com.kt.service;

import java.util.UUID;

public interface RefundService {
	void requestRefund(UUID userId, UUID orderProductId, String reason);

}
