package com.kt.exception.handler;

import com.kt.common.api.ApiErrorResponse;
import com.kt.constant.message.ErrorCode;
import com.kt.exception.BaseException;
import com.kt.exception.CustomException;

import com.kt.exception.handler.base.BaseExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class SpringExceptionHandler extends BaseExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiErrorResponse> customException(CustomException ex) {
		log.info("Exception Message : {}", ex.error().getMessage());
		return toResponse(ex);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.warn("[MethodArgumentNotValidException] message={}", e.getMessage());
		String details = Arrays.toString(e.getDetailMessageArguments());
		String message = details.split(",", 2)[1].replace("]", "").trim();
		ErrorCode error = ErrorCode.BODY_FIELD_ERROR;
		String resultMessage = error.format(message);
		return ApiErrorResponse.error(error.getStatus(), resultMessage);
	}
}
