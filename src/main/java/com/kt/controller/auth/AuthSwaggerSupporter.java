package com.kt.controller.auth;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;

import com.kt.domain.dto.request.LoginRequest;
import com.kt.domain.dto.request.PasswordManagementRequest;
import com.kt.domain.dto.request.SignupRequest;

import com.kt.domain.dto.request.TokenReissueRequest;
import com.kt.domain.dto.response.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "이메일 인증 코드 전송",
		description = "요청된 이메일로 인증 번호 전송 API"
	)
	ResponseEntity<ApiResult<Void>> sendAuthCode(SignupRequest.SignupEmail request);

	@Operation(
		summary = "이메일 인증 코드 검증",
		description = "이메일로 전송된 인증 코드 검증 API"
	)
	ResponseEntity<ApiResult<Void>> verifyAuthCode(SignupRequest.VerifySignupCode request);

	@Operation(
		summary = "유저 회원 가입",
		description = "유저 회원 가입 API"
	)
	ResponseEntity<ApiResult<Void>> signupUser(SignupRequest.SignupUser request);

	@Operation(
		summary = "기사 회원 가입",
		description = "기사 회원 가입 API"
	)
	ResponseEntity<ApiResult<Void>> signupCourier(SignupRequest.SignupCourier request);

	@Operation(
		summary = "계정 로그인",
		description = "계정 로그인 API"
	)
	ResponseEntity<ApiResult<TokenResponse>> login(LoginRequest request);

	@Operation(
		summary = "비밀번호 초기화",
		description = "비밀번호 초기화 API"
	)
	ResponseEntity<ApiResult<Void>> resetPassword(PasswordManagementRequest.PasswordReset request);

	@Operation(
		summary = "비밀번호 초기화 요청",
		description = "비밀번호 초기화 요청 API"
	)
	ResponseEntity<ApiResult<Void>> requestPasswordReset(PasswordManagementRequest.PasswordReset request);

	@Operation(
		summary = "비밀번호 변경 요청",
		description = "비밀번호 변경 요청 API"
	)
	ResponseEntity<ApiResult<Void>> requestPasswordUpdate(PasswordManagementRequest.PasswordUpdate request);

	@Operation(
		summary = "토큰 재발급 요청",
		description = "토큰 재발급 요청 API"
	)
	ResponseEntity<ApiResult<TokenResponse>> reissueToken(TokenReissueRequest request);

}