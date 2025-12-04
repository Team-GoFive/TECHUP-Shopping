package com.kt.exception;

import org.springframework.core.NestedRuntimeException;

import com.kt.constant.message.ErrorCode;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
public class BaseException extends NestedRuntimeException {
	@Accessors(fluent = true)
	private ErrorCode error;

	public BaseException(ErrorCode error) {
		super(error.name());
		this.error = error;
	}
}
