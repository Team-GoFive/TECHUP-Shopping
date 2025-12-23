package com.kt.service.admin;

import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.user.UserRepository;
import com.kt.util.Preconditions;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Override
	public Page<UserResponse.Search> getUsers(Pageable pageable, String keyword, AccountRole role) {
		return userRepository.searchUsers(pageable, keyword, role);
	}

	@Override
	public UserResponse.Orders getOrdersByUserId(UUID targetUserId) {
		userRepository.findByIdOrThrow(targetUserId);
		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(targetUserId);

		return UserResponse.Orders.of(targetUserId, orders);
	}

	@Override
	public void updateUserDetail(
		UUID requestedUserId,
		UserRequest.Update details
	) {

		UserEntity user = userRepository.findByIdOrThrow(requestedUserId);

		user.update(
			details.name(),
			details.email(),
			details.mobile(),
			details.birth()
		);
	}

	@Override
	public UserResponse.UserDetail getUserDetail(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
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
	public void enableUser(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
		user.enabled();
	}

	@Override
	public void disableUser(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
		user.disabled();
	}

	@Override
	public void deleteUser(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
		user.delete();
	}

	@Override
	public void retireUser(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
		user.retired();
	}

	@Override
	public void deleteUserPermanently(UUID targetUserId) {
		UserEntity user = userRepository.findByIdOrThrow(targetUserId);
		orderRepository.clearUser(user.getId());
		userRepository.delete(user);
	}

}
