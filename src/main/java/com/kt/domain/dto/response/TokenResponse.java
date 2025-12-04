package com.kt.domain.dto.response;

public record TokenResponse(
	String accessToken,
	String refreshToken
){
}
