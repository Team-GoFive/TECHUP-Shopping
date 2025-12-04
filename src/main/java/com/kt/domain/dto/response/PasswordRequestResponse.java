package com.kt.domain.dto.response;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;

import java.util.UUID;

public record PasswordRequestResponse(
	UUID passwordRequestId,
	UUID accountId,
	PasswordRequestType requestType,
	PasswordRequestStatus status
) {
}
