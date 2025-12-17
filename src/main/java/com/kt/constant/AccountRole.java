package com.kt.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountRole {

	ADMIN("관리자"),
	MEMBER("회원"),
	COURIER("기사"),
	SELLER("판매자");

	private final String description;

}
