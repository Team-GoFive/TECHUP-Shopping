package com.kt.domain.dto.request;

import com.kt.constant.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourierRequest {
	public record UpdateDetails(
		@NotBlank(message = "이름은 필수항목입니다.")
		String name,
		@NotNull(message = "성별은 필수 항목입니다.")
		Gender gender
	){
	}
}
