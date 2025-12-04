package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.domain.dto.request.AddressRequest;
import com.kt.domain.dto.response.AddressResponse;

public interface AddressService {

	UUID create(String email, AddressRequest request);

	List<AddressResponse> getMyAddresses(String email);

	AddressResponse getOne(String email, UUID addressId);

	void update(String email, UUID addressId, AddressRequest request);

	void delete(String email, UUID addressId);
}
