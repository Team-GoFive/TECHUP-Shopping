package com.kt.controller.address;

import static com.kt.common.api.ApiResult.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.dto.response.AddressResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController implements AddressSwaggerSupporter {

	private final AddressService addressService;

	@Override
	@PostMapping
	public ResponseEntity<ApiResult<UUID>> createAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody @Valid AddressRequest request
	) {
		return wrap(addressService.create(currentUser.getId(), request));
	}

	@Override
	@GetMapping
	public ResponseEntity<ApiResult<List<AddressResponse>>> getMyAddresses(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		return wrap(addressService.getMyAddresses(currentUser.getId()));
	}

	@Override
	@GetMapping("/{addressId}")
	public ResponseEntity<ApiResult<AddressResponse>> getAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId
	) {
		return wrap(addressService.getOne(currentUser.getId(), addressId));
	}

	@Override
	@PutMapping("/{addressId}")
	public ResponseEntity<ApiResult<Void>> updateAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId,
		@RequestBody @Valid AddressRequest request
	) {
		addressService.update(currentUser.getId(), addressId, request);
		return empty();
	}

	@Override
	@DeleteMapping("/{addressId}")
	public ResponseEntity<ApiResult<Void>> deleteAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId
	) {
		addressService.delete(currentUser.getId(), addressId);
		return empty();
	}
}