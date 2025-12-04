package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CourierWorkStatus {
	WORKING("근무중"),
	RESTING("근무종료");

	private final String description;
}
