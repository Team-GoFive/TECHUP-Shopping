package com.kt.service.admin;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.UserRole;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;

public interface AdminUserService {

	UserResponse.UserDetail getUserDetail(UUID currentId, UUID subjectId);

	void disableUser(UUID currentId, UUID subjectId);

	void enableUser(UUID currentId, UUID subjectId);

	void deleteUser(UUID currentId, UUID subjectId);

	void deleteUserPermanently(UUID currentId, UUID id);

	Page<UserResponse.Search> getUsers(UUID userId, Pageable pageable, String keyword, UserRole role);

	// TODO: 아래 메서드 AdminUserController 내 메서드 구현 필요
	UserResponse.Orders getOrdersByUserId(UUID currentId, UUID subjectId);

	void updateUserDetail(UUID currentUserId, UUID targetUserId, UserRequest.UpdateDetails details);

	// 어드민이 탈퇴 가능하게 할것인지?
	void retireUser(UUID currentId, UUID subjectId);

}
