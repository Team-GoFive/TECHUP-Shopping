package com.kt.controller.auth;

import com.kt.domain.dto.request.PasswordManagementRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.response.TokenResponse;
import com.kt.service.AuthService;
import com.mysema.commons.lang.Pair;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/email/code")
	public ResponseEntity<ApiResult<Void>> sendAuthCode(
		@RequestBody @Valid SignupRequest.SignupEmail request
	) {
		authService.sendAuthCode(request);
		return empty();
	}

	@PostMapping("/email/verify")
	public ResponseEntity<ApiResult<Void>> verifySignupCode(
		@RequestBody @Valid SignupRequest.VerifySignupCode request
	) {
		authService.verifySignupCode(request);
		return empty();
	}

	@PostMapping("/signup/member")
	public ResponseEntity<ApiResult<Void>> signupMember(
		@RequestBody @Valid SignupRequest.SignupMember request
	) {
		authService.signupMember(request);
		return empty();
	}

	@PostMapping("/signup/courier")
	public ResponseEntity<ApiResult<Void>> signupCourier(
		@RequestBody @Valid SignupRequest.SignupCourier request
	) {
		authService.signupCourier(request);
		return empty();
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResult<TokenResponse>> login(
		@RequestBody @Valid LoginRequest request
	) {
		Pair<String, String> tokens = authService.login(request);
		return wrap(
			new TokenResponse(
				tokens.getFirst(),
				tokens.getSecond()
			)
		);
	}

	@PatchMapping("/password-init")
	public ResponseEntity<ApiResult<Void>> initPassword(
		@RequestBody @Valid PasswordManagementRequest.PasswordInit request
	) {
		authService.initPassword(request);
		return empty();
	}

	@PostMapping("/password-init/requests")
	public ResponseEntity<ApiResult<Void>> requestPasswordInit(
		@RequestBody @Valid PasswordManagementRequest.PasswordInit request
	) {
		authService.requestPasswordInit(request);
		return empty();
	}

}
