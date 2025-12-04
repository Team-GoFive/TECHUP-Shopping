package com.kt.exception;

import com.kt.constant.message.ErrorCode;

public class CustomException extends BaseException {

	public CustomException(ErrorCode error) {
		super(error);
	}

}
