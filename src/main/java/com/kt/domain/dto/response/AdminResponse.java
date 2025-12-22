package com.kt.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class AdminResponse {

	@Schema(name = "AdminDetailResponse")
	public record Detail(
		String name,
		String email
	) {}
}
