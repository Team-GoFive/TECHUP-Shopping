package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {
	ACCESS("accessToken"),
	REFRESH("refreshToken");

	private final String description;
}
