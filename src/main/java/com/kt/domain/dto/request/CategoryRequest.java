package com.kt.domain.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

// TODO: 기능 명시 논의 후 request 및 기능 변경 필요
public class CategoryRequest {
	public record Create(
		@NotBlank
		String title,
		UUID parentId
	) {

	}

	public record Update(
		@NotBlank
		String title,
		UUID parentId
	) {

	}
}
