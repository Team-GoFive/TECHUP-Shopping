package com.kt.controller.user;

import org.springframework.http.ResponseEntity;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "유저 관련 API")
public interface UserSwaggerSupporter {

		@Operation(
		summary = "내 정보 상세 조회",
		description = "로그인한 유저의 상세정보 조회 관련 API"
		)
		ResponseEntity<ApiResult<UserResponse.UserDetail>> me (@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser);

		@Operation(
			summary = "내 정보 수정",
			description = "로그인한 유저의 정보 수정 관련 API"
		)
		ResponseEntity<ApiResult<Void>> updateUserBySelf(
			@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser,
			UserRequest.UpdateDetails request
		);

	@Operation(
		summary = "내가 작성하지 않은 리뷰를 조회",
		description = "로그인한 유저의 작성하지 않은 리뷰 조회 관련 API"
	)
	ResponseEntity<ApiResult<PageResponse<OrderProductResponse.SearchReviewable>>> searchReviewables(
		Paging paging,
		@Parameter(hidden = true) DefaultCurrentUser defaultCurrentUser
	);

}
