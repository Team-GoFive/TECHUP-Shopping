package com.kt.domain.dto.request;

import com.kt.constant.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class MemberRequest {

	public record SignupMember(
		@NotBlank(message = "이름은 필수 항목입니다.")
		String name,

		@Email(message = "올바른 이메일 형식이 아닙니다.")
		@NotBlank(message = "이메일은 필수 항목입니다.")
		String email,

		@NotBlank(message = "비밀번호는 필수 항목입니다.")
		String password,

		@NotNull(message = "성별은 필수 항목입니다.")
		Gender gender,

		@NotNull(message = "생일날짜는 필수 항목입니다.")
		LocalDate birth,

		@NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
		@Pattern(
			regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$",
			message = "올바른 휴대폰 번호 형식이 아닙니다."
		)
		String mobile
	) {
	}
}
