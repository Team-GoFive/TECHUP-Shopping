package com.kt.common.api;

import lombok.Getter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ApiErrorResponse extends ApiResponse {

	public ApiErrorResponse(String code, String message) {
		super(code, message);
	}

	public static ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
		return ResponseEntity.status(status)
			.body(
				new ApiErrorResponse(
					status.series().name(),
					message
				)
			);
	}

}
