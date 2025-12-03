package com.kt.common;

import static java.util.UUID.*;

import com.kt.constant.Gender;
import com.kt.domain.dto.request.SignupRequest;

public class SignupCourierRequestCreator {

	public static final String EMAIL_DOMAIN = "@test.com";

	public static SignupRequest.SignupCourier createSignupCourierRequest() {
		return new SignupRequest.SignupCourier(
			"테스트기사",
			randomUUID() + EMAIL_DOMAIN,
			"1231231!",
			Gender.MALE
		);
	}

	public static SignupRequest.SignupCourier createSignupCourierRequest(String email) {
		return new SignupRequest.SignupCourier(
			"테스트기사",
			email,
			"1231231!",
			Gender.MALE
		);
	}
}
