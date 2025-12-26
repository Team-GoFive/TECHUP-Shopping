package com.kt.exception.handler;

import com.kt.common.api.ApiErrorResponse;
import com.kt.constant.message.ErrorCode;
import com.kt.exception.FieldValidationException;
import com.kt.exception.handler.base.BaseExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler extends BaseExceptionHandler {

	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleFieldValid(FieldValidationException ex) {
		String errorMessage = ex.getErrorMessage();
		log.warn("[FieldValidationException] message = {}", errorMessage);
		return toResponse(ex.error().getStatus(), errorMessage);
	}

}
