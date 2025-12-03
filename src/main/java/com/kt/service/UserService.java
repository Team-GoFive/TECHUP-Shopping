package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.constant.UserRole;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;

public interface UserService {
	Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId);

	UserResponse.Orders getOrdersByUserId(UUID id);

	Page<UserResponse.Search> getUsers(Pageable pageable, String keyword, UserRole role);

	UserResponse.UserDetail getUserDetail(UUID id);

	UserResponse.UserDetail getAdminDetail(UUID id);

	void disableUser(UUID id);

	void enableUser(UUID id);

	void deleteUser(UUID id);

	void deleteUserPermanently(UUID id);

	void retireUser(UUID id);

	void createAdmin(SignupRequest.SignupMember request);

	void deleteAdmin(UUID adminId);

	void updateUserDetail(String email, UUID userId, UserRequest.UpdateDetails details);
}
