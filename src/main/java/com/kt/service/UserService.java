package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;

public interface UserService {
	Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId);

	UserResponse.Orders getOrdersByUserId(UUID currentId, UUID subjectId);

	Page<UserResponse.Search> getUsers(UUID userId, Pageable pageable, String keyword, AccountRole role);

	UserResponse.UserDetail getUserDetail(UUID currentId, UUID subjectId);

	UserResponse.UserDetail getUserDetailSelf(UUID currentId);

	UserResponse.UserDetail getAdminDetail(UUID currentId, UUID subjectId);

	void disableUser(UUID currentId, UUID subjectId);

	void enableUser(UUID currentId, UUID subjectId);

	void deleteUser(UUID currentId, UUID subjectId);

	void deleteUserPermanently(UUID currentId, UUID id);

	void retireUser(UUID currentId, UUID subjectId) ;

	void createAdmin(UUID userId, SignupRequest.SignupUser request);

	void deleteAdmin(UUID currentId, UUID adminId);

	void updateUserDetail(UUID currentUserId, UUID targetUserId, UserRequest.UpdateDetails details);

	void updateUserDetailSelf(UUID currentUserId, UserRequest.UpdateDetails details);
}
