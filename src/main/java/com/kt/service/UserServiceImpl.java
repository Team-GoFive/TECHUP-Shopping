package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.user.UserRepository;
import com.kt.util.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final OrderProductRepository orderProductRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Override
	public UserResponse.Orders getOrdersByUserId(UUID currentUserId) {
		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(currentUserId);

		return UserResponse.Orders.of(currentUserId, orders);
	}

	@Override
	public Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId) {
		return orderProductRepository.getReviewableOrderProductsByUserId(pageable, userId);
	}

	@Override
	public UserResponse.UserDetail detail(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		return new UserResponse.UserDetail(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getRole(),
			user.getGender(),
			user.getBirth(),
			user.getMobile()
		);
	}

	@Override
	public UserResponse.UserDetail getUserDetailSelf(UUID currentId) {
		UserEntity user = userRepository.findByIdOrThrow(currentId);
		return new UserResponse.UserDetail(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getRole(),
			user.getGender(),
			user.getBirth(),
			user.getMobile()
		);
	}

	// TODO: for admin
	@Override
	public void deleteUser(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		user.delete();
	}

	// TODO: for admin
	@Override
	public void retireUser(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		user.retired();
	}

	@Override
	public void update(UUID currentUserId, UserRequest.Update request) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);

		user.update(
			request.name(),
			request.email(),
			request.mobile(),
			request.birth()
		);
	}

}
