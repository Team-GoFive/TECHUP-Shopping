package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserStatus {
	ENABLED("활성"),
	DISABLED("비활성"),
	RETIRED("탈퇴"),
	DELETED("삭제");

	private final String description;
}
