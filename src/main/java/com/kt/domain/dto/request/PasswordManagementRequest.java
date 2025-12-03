package com.kt.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class PasswordManagementRequest {

	@Schema(name = "PasswordResetRequest")
	public record PasswordReset(
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		@NotBlank(message = "이메일은 필수 항목입니다.")
		String email
	) {
	}

	@Schema(name = "PasswordUpdateRequest")
	public record PasswordUpdate(
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		@NotBlank(message = "이메일은 필수 항목입니다.")
		String email,

		@NotBlank(message = "이메일은 필수 항목입니다.")
		String password
	) {
	}
}
