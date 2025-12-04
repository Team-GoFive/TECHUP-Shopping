package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReviewStatus {
	ENABLED("활성"),
	REMOVED("삭제");

	private final String description;
}
