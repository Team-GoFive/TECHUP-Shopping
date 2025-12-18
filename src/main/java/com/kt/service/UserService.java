package com.kt.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;

public interface UserService {
	Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId);

	UserResponse.Orders getOrdersByUserId(UUID currentUserId);

	UserResponse.UserDetail detail(UUID currentUserId);

	UserResponse.UserDetail getUserDetailSelf(UUID currentUserId);

	void deleteUser(UUID currentUserId);

	void retireUser(UUID currentUserId);


	void update(UUID currentUserId, UserRequest.Update request);
}
