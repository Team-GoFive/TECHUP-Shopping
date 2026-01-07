package com.kt.common;

import static java.util.UUID.*;

import java.time.LocalDate;

import com.kt.constant.Gender;
import com.kt.domain.dto.request.SignupRequest;

public class SignupUserRequestCreator {

	public static SignupRequest.SignupUser createSignupUserRequest() {
		return new SignupRequest.SignupUser(
			"테스트회원",
			randomUUID() + "@test.com",
			"1231231!",
			Gender.MALE,
			LocalDate.now(),
			"010-1234-1234"
		);
	}

	public static SignupRequest.SignupUser createSignupUserRequest(
		String email
	) {
		return new SignupRequest.SignupUser(
			"테스트회원",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.now(),
			"010-1234-1234"
		);
	}
}
