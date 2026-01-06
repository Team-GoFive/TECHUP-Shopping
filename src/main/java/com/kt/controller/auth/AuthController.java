package com.kt.controller.auth;

import static com.kt.common.api.ApiResult.*;

import com.kt.service.user.UserSignupService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.TokenReissueRequest;
import com.kt.domain.dto.response.TokenResponse;
import com.kt.service.AuthService;
import com.mysema.commons.lang.Pair;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwaggerSupporter {

	private final AuthService authService;
	private final UserSignupService userSignupService;

	@PostMapping("/email/code")
	public ResponseEntity<ApiResult<Void>> sendAuthCode(
		@RequestBody @Valid SignupRequest.SignupEmail request
	) {
		authService.sendAuthCode(request);
		return empty();
	}

	@PostMapping("/email/verify")
	public ResponseEntity<ApiResult<Void>> verifyAuthCode(
		@RequestBody @Valid SignupRequest.VerifySignupCode request
	) {
		authService.verifySignupCode(request);
		return empty();
	}

	@PostMapping("/signup/user")
	public ResponseEntity<ApiResult<Void>> signupUser(
		@RequestBody @Valid SignupRequest.SignupUser request
	) {
		userSignupService.signupUser(request);
		return empty();
	}

	@PostMapping("/signup/seller")
	public ResponseEntity<ApiResult<Void>> signupSeller(
		@RequestBody @Valid SignupRequest.SignupSeller request
	) {
		authService.signupSeller(request);
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

	@PatchMapping("/password/reset")
	public ResponseEntity<ApiResult<Void>> resetPassword(
		@RequestBody @Valid PasswordManagementRequest.PasswordReset request
	) {
		authService.resetPassword(request);
		return empty();
	}

	@PostMapping("/password/reset-requests")
	public ResponseEntity<ApiResult<Void>> requestPasswordReset(
		@RequestBody @Valid PasswordManagementRequest.PasswordReset request
	) {
		authService.requestPasswordReset(request);
		return empty();
	}

	@PostMapping("/password-update/requests")
	public ResponseEntity<ApiResult<Void>> requestPasswordUpdate(
		@RequestBody @Valid PasswordManagementRequest.PasswordUpdate request
	) {
		authService.requestPasswordUpdate(request);
		return empty();
	}

	@PostMapping("/token/reissue")
	public ResponseEntity<ApiResult<TokenResponse>> reissueToken(
		@RequestBody TokenReissueRequest request
	) {
		Pair<String, String> tokens = authService.reissueToken(request);
		return wrap(
			new TokenResponse(
				tokens.getFirst(),
				tokens.getSecond()
			)
		);
	}

}
