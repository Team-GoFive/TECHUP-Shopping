package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatStatus {
	WAITING("상담사 전환 대기"),
	CONNECTED("상담사 연결"),
	CLOSED("상담 종료"),
	;

	private final String description;
}
