package com.kt.controller.address;

import static com.kt.common.api.ApiResult.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
public class AddressController {

	private final AddressService addressService;

	@PostMapping
	ResponseEntity<ApiResult<UUID>> createAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@Valid @RequestBody AddressRequest request
	) {
		return wrap(addressService.create(currentUser.getUsername(), request));
	}

	@GetMapping
	ResponseEntity<ApiResult<List<AddressResponse>>> getMyAddresses(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		return wrap(addressService.getMyAddresses(currentUser.getUsername()));
	}

	@GetMapping("/{addressId}")
	ResponseEntity<ApiResult<AddressResponse>> getAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId
	) {
		return wrap(addressService.getOne(currentUser.getUsername(), addressId));
	}

	@PutMapping("/{addressId}")
	ResponseEntity<ApiResult<Void>> updateAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId,
		@Valid @RequestBody AddressRequest request
	) {
		addressService.update(currentUser.getUsername(), addressId, request);
		return empty();
	}

	@DeleteMapping("/{addressId}")
	ResponseEntity<ApiResult<Void>> deleteAddress(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@PathVariable UUID addressId
	) {
		addressService.delete(currentUser.getUsername(), addressId);
		return empty();
	}
}