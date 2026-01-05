package com.kt.controller.auth;

import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.dto.request.PasswordManagementRequest;

import com.kt.domain.dto.request.TokenReissueRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwaggerSupporter {

	private final AuthService authService;

	@Operation(
		summary = "이메일 인증 코드 전송",
		description = "요청된 이메일로 인증 번호 전송 API"
	)
	@PostMapping("/email/code")
	public ResponseEntity<ApiResult<Void>> sendAuthCode(
		@RequestBody @Valid SignupRequest.SignupEmail request
	) {
		authService.sendAuthCode(request);
		return empty();
	}

	@Operation(
		summary = "이메일 인증 코드 검증",
		description = "이메일로 전송된 인증 코드 검증 API"
	)
	@PostMapping("/email/verify")
	public ResponseEntity<ApiResult<Void>> verifyAuthCode(
		@RequestBody @Valid SignupRequest.VerifySignupCode request
	) {
		authService.verifySignupCode(request);
		return empty();
	}

	@Operation(
		summary = "유저 회원 가입",
		description = "유저 회원 가입 API"
	)
	@PostMapping("/signup/user")
	public ResponseEntity<ApiResult<Void>> signupUser(
		@RequestBody @Valid SignupRequest.SignupUser request
	) {
		authService.signupUser(request);
		return empty();
	}

	@Operation(
		summary = "기사 회원 가입",
		description = "기사 회원 가입 API"
	)
	@PostMapping("/signup/courier")
	public ResponseEntity<ApiResult<Void>> signupCourier(
		@RequestBody @Valid SignupRequest.SignupCourier request
	) {
		authService.signupCourier(request);
		return empty();
	}

	@Operation(
		summary = "계정 로그인",
		description = "계정 로그인 API"
	)
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

	@Operation(
		summary = "비밀번호 초기화",
		description = "비밀번호 초기화 API"
	)
	@PatchMapping("/password/reset")
	public ResponseEntity<ApiResult<Void>> resetPassword(
		@RequestBody @Valid PasswordManagementRequest.PasswordReset request
	) {
		authService.resetPassword(request);
		return empty();
	}

	@Operation(
		summary = "비밀번호 초기화 요청",
		description = "비밀번호 초기화 요청 API"
	)
	@PostMapping("/password/reset-requests")
	public ResponseEntity<ApiResult<Void>> requestPasswordReset(
		@RequestBody @Valid PasswordManagementRequest.PasswordReset request
	) {
		authService.requestPasswordReset(request);
		return empty();
	}

	@Operation(
		summary = "비밀번호 변경 요청",
		description = "비밀번호 변경 요청 API"
	)
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
