package com.kt.domain.dto.request;

import com.kt.constant.AccountRole;
import com.kt.constant.CourierWorkStatus;

import com.kt.constant.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AccountRequest {
	@Schema(name="AccountUpdatePasswordRequest")
	public record UpdatePassword(
		@NotBlank(message = "현재 비밀번호는 필수항목입니다.")
		String currentPassword,
		@NotBlank(message = "새로운 비밀번호는 필수항목입니다.")
		String newPassword
	){
	}
	@Schema(name="AccountSearchRequest")
	public record Search(
		AccountRole role,
		UserStatus userStatus,
		CourierWorkStatus courierWorkStatus,
		String searchKeyword
	){
	}
}
