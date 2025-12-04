package com.kt.exception.handler.base;

import com.kt.common.api.ApiErrorResponse;

import com.kt.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class BaseExceptionHandler {
	public ResponseEntity<ApiErrorResponse> toResponse(BaseException ex) {
		HttpStatus status = ex.error().getStatus();
		String message = ex.error().getMessage();
		log.info("message : {}", message);
		return toResponse(status, message);
	}

	public ResponseEntity<ApiErrorResponse> toResponse(HttpStatus status, String message) {
		return ApiErrorResponse.error(status, message);
	}
}
