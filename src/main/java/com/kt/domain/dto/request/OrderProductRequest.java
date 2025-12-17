package com.kt.domain.dto.request;

import com.kt.constant.OrderProductStatus;

public class OrderProductRequest {
	public record ForceChangeStatus(
		OrderProductStatus status
	) {}
}