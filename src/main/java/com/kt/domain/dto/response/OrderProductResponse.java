package com.kt.domain.dto.response;

import java.util.UUID;

import com.kt.constant.OrderProductStatus;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProductResponse {
	@Schema(name = "SellerOrderProductResponse")
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
