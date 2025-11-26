package com.kt.service;

import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.TokenReissueRequest;
import com.mysema.commons.lang.Pair;

public interface AuthService {

	void signupUser(SignupRequest.SignupUser request);

	void signupCourier(SignupRequest.SignupCourier request);

	Pair<String, String> login(LoginRequest request);

	void sendAuthCode(SignupRequest.SignupEmail request);

	void verifySignupCode(SignupRequest.VerifySignupCode request);

	void initPassword(PasswordManagementRequest.PasswordReset request);

	void requestPasswordReset(PasswordManagementRequest.PasswordReset request);

	void requestPasswordUpdate(PasswordManagementRequest.PasswordUpdate request);

	Pair<String, String> reissueToken(TokenReissueRequest request);
}
