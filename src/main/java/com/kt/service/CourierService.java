package com.kt.service;

import java.util.UUID;

import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;

public interface CourierService {
	CourierResponse.Detail getDetail(UUID courierId);

	void updateDetail(UUID courierId, CourierRequest.UpdateDetails updateDetail);
	CourierResponse.CourierDetail getCourierDetail(UUID courierId);
}
