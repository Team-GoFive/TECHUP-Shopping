package com.kt.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ReviewRequest {
	@Schema(name = "ReviewCreateRequest")
	public record Create(
		@NotBlank(message = "리뷰내용은 필수 항목입니다.")
		String content
	) {
	}

	@Schema(name = "ReviewUpdateRequest")
	public record Update(
		@NotBlank(message = "리뷰내용은 필수 항목입니다.")
		String content
	) {
	}
}
