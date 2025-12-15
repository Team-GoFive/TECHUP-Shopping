package com.kt.service.admin;

import java.util.UUID;

import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;

public interface AdminCourierService {
	CourierResponse.DetailAdmin getDetail(UUID currentId, UUID subjectId);

	void updateDetail(UUID currentId, UUID subjectId, CourierRequest.UpdateDetails updateDetail);
}
