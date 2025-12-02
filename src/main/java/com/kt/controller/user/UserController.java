package com.kt.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;


@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends SwaggerAssistance {
	private final UserService userService;

	@Operation(
		summary = "내 정보 상세 조회",
		description = "로그인한 유저의 상세정보 조회 관련 API"
	)
	@GetMapping
	public ResponseEntity<ApiResult<UserResponse.UserDetail>> me(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	){
		return wrap(
			userService.getUserDetail(defaultCurrentUser.getId())
		);
	}

	@PutMapping
	@Operation(
		summary = "내 정보 수정",
		description = "로그인한 유저의 정보 수정 관련 API"
	)
	public ResponseEntity<ApiResult<Void>> updateUserBySelf(
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid UserRequest.UpdateDetails request
	){
		userService.updateUserDetail(
			defaultCurrentUser.getId(),
			request
		);
		return empty();
	}

	@GetMapping("/reviewable-products")
	@Operation(
		summary = "내가 작성하지 않은 리뷰를 조회",
		description = "로그인한 유저의 작성하지 않은 리뷰 조회 관련 API"
	)
	public ResponseEntity<ApiResult<PageResponse<OrderProductResponse.SearchReviewable>>> searchReviewables(
		@ModelAttribute Paging paging,
		@AuthenticationPrincipal @Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	){
		return page(
			userService.getReviewableOrderProducts(
				paging.toPageable(),
				defaultCurrentUser.getId())
		);
	}
}
