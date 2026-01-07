package com.kt.service.courier;

import java.util.UUID;

import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;

public interface CourierService {
	CourierResponse.Detail getDetail(UUID currentId, UUID subjectId);

	CourierResponse.DetailAdmin getDetailForAdmin(UUID currentId, UUID subjectId);

	void updateDetail(UUID currentId, UUID subjectId, CourierRequest.UpdateDetails updateDetail);
}
