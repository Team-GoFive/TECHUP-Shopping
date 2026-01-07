package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.ReceiverVO;
import com.querydsl.core.annotations.QueryProjection;

public class SellerOrderResponse {
	public record Search(
		UUID orderId,
		UUID ordererId,
		String ordererName,
		UUID orderProductId,
		UUID productId,
		String productName,
		Long quantity,
		ReceiverVO receiverVO,
		OrderProductStatus status,
		Instant createdAt
	) {
		@QueryProjection
		public Search {
		}
	}
}
