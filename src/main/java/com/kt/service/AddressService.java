package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.dto.response.AddressResponse;

public interface AddressService {

	UUID create(UUID userId, AddressRequest request);

	List<AddressResponse> getMyAddresses(UUID userId);

	AddressResponse getOne(UUID userId, UUID addressId);

	void update(UUID userId, UUID addressId, AddressRequest request);

	void delete(UUID userId, UUID addressId);
}
