package com.kt.domain.dto.response;

public record TokenReissueResponse(
	String accessToken,
	String refreshToken
) {
}
