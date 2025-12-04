package com.kt.constant.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultCode {

	SUCCESS("ok", "성공");

	private final String code;
	private final String message;
}
