package com.kt.domain.dto.response;

import java.util.UUID;

import com.kt.constant.OrderProductStatus;
import com.querydsl.core.annotations.QueryProjection;

public class OrderProductResponse {
	public record SearchReviewable(
		UUID orderProductId,
		Long quantity,
		Long unitPrice,
		OrderProductStatus status
	) {
		@QueryProjection
		public SearchReviewable {
		}
	}
}
