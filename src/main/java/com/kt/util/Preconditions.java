package com.kt.util;

import com.kt.constant.message.ErrorCode;
import com.kt.exception.CustomException;

public final class Preconditions {
	public static void validate(boolean expression, ErrorCode errorCode) {
		if (!expression) {
			throw new CustomException(errorCode);
		}
	}
}

