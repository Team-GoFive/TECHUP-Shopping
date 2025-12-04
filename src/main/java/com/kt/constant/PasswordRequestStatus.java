package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PasswordRequestStatus {
	PENDING("요청"),
	COMPLETED("완료");

	private final String description;
}
