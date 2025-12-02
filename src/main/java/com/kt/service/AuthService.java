package com.kt.service;

import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.mysema.commons.lang.Pair;

public interface AuthService {

	void signupMember(SignupRequest.SignupMember request);

	void signupCourier(SignupRequest.SignupCourier request);

	Pair<String, String> login(LoginRequest request);

	void sendAuthCode(SignupRequest.SignupEmail request);

	void verifySignupCode(SignupRequest.VerifySignupCode request);

	void resetPassword(PasswordManagementRequest.PasswordReset request);

	void requestResetPassword(PasswordManagementRequest.PasswordReset request);

	void requestUpdatePassword(PasswordManagementRequest.PasswordUpdate request);
}
