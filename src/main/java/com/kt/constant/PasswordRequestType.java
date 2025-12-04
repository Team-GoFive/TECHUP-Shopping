package com.kt.constant;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum PasswordRequestType {
	UPDATE("변경요청"),
	RESET("초기화요청");

	private final String description;
}
