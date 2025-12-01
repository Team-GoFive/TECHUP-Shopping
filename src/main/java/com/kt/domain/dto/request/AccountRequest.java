package com.kt.domain.dto.request;



import com.kt.constant.CourierWorkStatus;
import com.kt.constant.UserRole;

import com.kt.constant.UserStatus;

import jakarta.validation.constraints.NotBlank;

public class AccountRequest {
		public record UpdatePassword(
			@NotBlank(message = "현재 비밀번호는 필수항목입니다.")
			String currentPassword,
			@NotBlank(message = "새로운 비밀번호는 필수항목입니다.")
			String newPassword
		){
		}

		public record Search(
			UserRole role,
			UserStatus userStatus,
			CourierWorkStatus courierWorkStatus,
			String searchKeyword
		){
		}
}
