package com.kt.controller.address;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.kt.common.api.ApiResult;
import com.kt.common.support.SwaggerSupporter;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.dto.response.AddressResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Address", description = "사용자 주소 관리 API")
public interface AddressSwaggerSupporter extends SwaggerSupporter {

	@Operation(
		summary = "주소 생성",
		description = "로그인한 사용자가 새로운 배송지를 생성하는 API"
	)
	ResponseEntity<ApiResult<UUID>> createAddress(
		DefaultCurrentUser currentUser,
		AddressRequest request
	);

	@Operation(
		summary = "내 주소 목록 조회",
		description = "로그인한 사용자가 자신의 배송지 목록을 조회하는 API"
	)
	ResponseEntity<ApiResult<List<AddressResponse>>> getMyAddresses(
		DefaultCurrentUser currentUser
	);

	@Operation(
		summary = "주소 상세 조회",
		description = "로그인한 사용자가 특정 배송지를 조회하는 API",
		parameters = {
			@Parameter(name = "addressId", description = "주소 ID")
		}
	)
	ResponseEntity<ApiResult<AddressResponse>> getAddress(
		DefaultCurrentUser currentUser,
		UUID addressId
	);

	@Operation(
		summary = "주소 수정",
		description = "로그인한 사용자가 자신의 배송지 정보를 수정하는 API",
		parameters = {
			@Parameter(name = "addressId", description = "주소 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> updateAddress(
		DefaultCurrentUser currentUser,
		UUID addressId,
		AddressRequest request
	);

	@Operation(
		summary = "주소 삭제",
		description = "로그인한 사용자가 자신의 배송지를 삭제하는 API",
		parameters = {
			@Parameter(name = "addressId", description = "주소 ID")
		}
	)
	ResponseEntity<ApiResult<Void>> deleteAddress(
		DefaultCurrentUser currentUser,
		UUID addressId
	);

}