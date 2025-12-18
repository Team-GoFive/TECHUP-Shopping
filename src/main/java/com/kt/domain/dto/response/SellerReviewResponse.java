package com.kt.domain.dto.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;

public class SellerReviewResponse {
	public record search(
		UUID productId,
		String productName,
		UUID userId,
		String userName,
		UUID reviewId,
		String content
	) {
		@QueryProjection
		public search {

		}
	}
}
