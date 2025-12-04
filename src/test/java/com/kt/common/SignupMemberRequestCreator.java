package com.kt.common;

import static java.util.UUID.*;

import java.time.LocalDate;

import com.kt.constant.Gender;
import com.kt.domain.dto.request.SignupRequest;

public class SignupMemberRequestCreator {

	public static SignupRequest.SignupMember createSignupMemberRequest() {
		return new SignupRequest.SignupMember(
			"테스트회원",
			randomUUID() + "@test.com",
			"1231231!",
			Gender.MALE,
			LocalDate.now(),
			"010-1234-1234"
		);
	}

	public static SignupRequest.SignupMember createSignupMemberRequest(
		String email
	) {
		return new SignupRequest.SignupMember(
			"테스트회원",
			email,
			"1231231!",
			Gender.MALE,
			LocalDate.now(),
			"010-1234-1234"
		);
	}
}
