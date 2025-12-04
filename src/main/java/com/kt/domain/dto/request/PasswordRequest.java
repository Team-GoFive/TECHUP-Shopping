package com.kt.domain.dto.request;

import com.kt.constant.PasswordRequestStatus;

import com.kt.constant.PasswordRequestType;

import com.kt.constant.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

public class PasswordRequest {

	@Schema(name = "PasswordRequestsSearch")
	public record Search(
		UserRole role,
		PasswordRequestStatus status,
		PasswordRequestType requestType,
		String searchKeyword
	) {

	}
}
