package com.kt.domain.dto.response;

import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.querydsl.core.annotations.QueryProjection;

public class AccountResponse {

	public record Search(
		String name,
		String email,
		UserStatus userStatus,
		UserRole role
	) {
		@QueryProjection
		public Search {
		}
	}
}
