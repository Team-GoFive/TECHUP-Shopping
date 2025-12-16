package com.kt.service.admin;

import java.util.UUID;

import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;

public interface AdminManagementService {
	void createAdmin(UUID userId, SignupRequest.SignupUser request);

	void deleteAdmin(UUID currentId, UUID adminId);

	UserResponse.UserDetail getAdminDetail(UUID currentId, UUID subjectId);

	void updateDetail(
		UUID currentId,
		UUID subjectId,
		UserRequest.UpdateDetails details
	);

}
