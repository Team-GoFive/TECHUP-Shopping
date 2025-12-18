package com.kt.service.admin;

import java.util.UUID;

import com.kt.constant.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;

public interface AdminUserService {

	UserResponse.UserDetail getUserDetail(UUID targetUserId);

	void disableUser(UUID targetUserId);

	void enableUser(UUID targetUserId);

	void deleteUser(UUID targetUserId);

	void deleteUserPermanently(UUID targetUserId);

	Page<UserResponse.Search> getUsers(Pageable pageable, String keyword, AccountRole role);

	UserResponse.Orders getOrdersByUserId(UUID targetUserId);

	void updateUserDetail(UUID currentUserId, UUID targetUserId, UserRequest.UpdateDetails details);

	void retireUser(UUID subjectId);

}
