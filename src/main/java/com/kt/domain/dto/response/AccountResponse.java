package com.kt.domain.dto.response;

import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;

public class AccountResponse {
	@Schema(name="AccountSearchResponse")
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
