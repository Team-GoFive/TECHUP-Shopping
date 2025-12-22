package com.kt.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminRequest {

	@Schema(name = "AdminUpdateRequest")
	public record Update(
		@NotBlank(message = "이름은 필수항목입니다.")
		String name,

		@NotBlank(message = "이메일은 필수항목입니다.")
		@Email(message = "올바른 이메일 형식이 아닙니다.")
		String email
	) {
	}

}
