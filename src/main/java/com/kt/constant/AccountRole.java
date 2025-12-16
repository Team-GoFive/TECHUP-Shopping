package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountRole {

	ADMIN("관리자"),
	MEMBER("회원"),
	COURIER("기사");

	private final String description;

}
