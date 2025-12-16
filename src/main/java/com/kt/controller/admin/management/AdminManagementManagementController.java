package com.kt.controller.admin.management;

import java.util.UUID;

import com.kt.common.api.PageResponse;

import com.kt.domain.dto.request.AccountRequest;

import com.kt.domain.dto.response.AccountResponse;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.AccountService;
import com.kt.service.admin.AdminManagementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminManagementManagementController implements AdminManagementSwaggerSupporter {

	private final AdminManagementService adminManagementService;
	private final AccountService accountService;

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<AccountResponse.Search>>> searchAdmins(
		@ParameterObject AccountRequest.Search request,
		@ModelAttribute Paging paging
	) {
		return page(
			accountService.searchAccounts(
				request,
				paging.toPageable()
			)
		);
	}

	@Override
	@GetMapping("/{adminId}")
	public ResponseEntity<ApiResult<UserResponse.UserDetail>> getAdminDetail(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID adminId
	) {
		return wrap(
			adminManagementService.getAdminDetail(currentUser.getId(), adminId)
		);
	}

	@Override
	@PostMapping
	public ResponseEntity<ApiResult<Void>> createAdmin(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid SignupRequest.SignupUser request
	) {
		adminManagementService.createAdmin(currentUser.getId(), request);
		return empty();
	}

	@Override
	@PutMapping("/{adminId}")
	public ResponseEntity<ApiResult<Void>> updateAdmin(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid UserRequest.UpdateDetails request,
		@PathVariable UUID adminId
	) {
		adminManagementService.updateDetail(currentUser.getId(), adminId, request);
		return empty();
	}

	@Override
	@DeleteMapping("/{adminId}")
	public ResponseEntity<ApiResult<Void>> deleteAdmin(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable UUID adminId
	) {
		adminManagementService.deleteAdmin(defaultCurrentUser.getId(), adminId);
		return empty();
	}

}
