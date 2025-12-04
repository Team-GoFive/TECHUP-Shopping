package com.kt.domain.dto.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReviewResponse {
	@Schema(name = "ReviewSearchResponse")
	public record Search(
		UUID reviewId,
		String content
	) {
		@QueryProjection
		public Search {
		}
	}
}
