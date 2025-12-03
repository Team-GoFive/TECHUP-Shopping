package com.kt.controller.admin.user;

import static com.kt.common.api.ApiResult.*;
import static com.kt.common.api.ApiResult.empty;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController implements AdminUserSwaggerSupporter {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResult<UserResponse.UserDetail>> getAccountDetail(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	) {
		return wrap(userService.getUserDetail(defaultCurrentUser.getId(), userId));
	}

	@PatchMapping("/{userId}/enabled")
	public ResponseEntity<ApiResult<Void>> enableAccount(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	) {
		userService.enableUser(defaultCurrentUser.getId(), userId);
		return empty();
	}

	@PatchMapping("/{userId}/disabled")
	public ResponseEntity<ApiResult<Void>> disableAccount(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	) {
		userService.disableUser(defaultCurrentUser.getId(), userId);
		return empty();
	}

	@PatchMapping("/{userId}/removed")
	public ResponseEntity<ApiResult<Void>> deleteAccount(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID userId
	) {
		userService.deleteUser(defaultCurrentUser.getId(), userId);
		return empty();
	}

}
