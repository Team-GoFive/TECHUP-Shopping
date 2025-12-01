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
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResult<UserResponse.UserDetail>> me(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	){
		return wrap(
			userService.getUserDetail(defaultCurrentUser.getId())
		);
	}

	@PutMapping
	public ResponseEntity<ApiResult<Void>> updateUserBySelf(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid UserRequest.UpdateDetails request
	){
		userService.updateUserDetail(
			defaultCurrentUser.getId(),
			request
		);
		return empty();
	}

	@GetMapping("/orderproducts/reviewable")
	public ResponseEntity<ApiResult<PageResponse<OrderProductResponse.SearchReviewable>>> searchReviewables(
		@ModelAttribute Paging paging,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	){
		return page(
			userService.getReviewableOrderProducts(
				paging.toPageable(),
				defaultCurrentUser.getId())
		);
	}

	@GetMapping("/reviews")
	public ResponseEntity<ApiResult<PageResponse<ReviewResponse.Search>>> searchReviews(
		@ModelAttribute Paging paging,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	){
		return page(
			userService.getReviewsByUserId(
				paging.toPageable(),
				defaultCurrentUser.getId())
		);
	}
}
