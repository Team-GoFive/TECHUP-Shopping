package com.kt.domain.dto.response;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.querydsl.core.annotations.QueryProjection;

import java.util.UUID;

public class PasswordRequestResponse {
	public record Search(
		UUID passwordRequestId,
		UUID accountId,
		PasswordRequestType requestType,
		PasswordRequestStatus status
	) {
		@QueryProjection
		public Search {
		}
	}
}
