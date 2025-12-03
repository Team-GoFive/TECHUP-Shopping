package com.kt.domain.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com.kt.constant.CourierWorkStatus;
import com.kt.constant.Gender;
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

	public record UserDetail(
		UUID id,
		String name,
		String email,
		Gender gender,
		UserStatus status,
		LocalDate birth,
		String mobile
	) {
	}

	public record CourierDetail(
		UUID id,
		String name,
		String email,
		Gender gender,
		UserStatus status,
		CourierWorkStatus workStatus
	) {
	}

}
