package com.kt.common.api;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kt.constant.message.SuccessCode;

import lombok.Getter;

@Getter
public class ApiResult<T> extends ApiResponse{

	final static HttpStatus SUCCESS = HttpStatus.OK;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;


	public ApiResult(String code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public static ResponseEntity<ApiResult<Void>> empty() {
		return wrap(null);
	}

	public static <T> ResponseEntity<ApiResult<T>> wrap(T data) {
		return ResponseEntity.status(SUCCESS)
			.body(
				new ApiResult<>(
					SuccessCode.RESULT.getCode(),
					SuccessCode.RESULT.getMessage(),
					data
				)
			);
	}

	public static <T> ResponseEntity<ApiResult<PageResponse<T>>> page(Page<T> data) {
		return wrap(new PageResponse<>(data));
	}


}
