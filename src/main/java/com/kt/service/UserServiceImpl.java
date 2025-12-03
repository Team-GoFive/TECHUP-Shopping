package com.kt.service;

import java.util.List;
import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.UserRole;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.review.ReviewRepository;
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
	private final PasswordEncoder passwordEncoder;
	private final AccountRepository accountRepository;


	@Override
	public UserResponse.Orders getOrdersByUserId(UUID id) {
		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(id);
		return UserResponse.Orders.of(id, orders);
	}

	@Override
	public Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId) {
		return orderProductRepository.getReviewableOrderProductsByUserId(pageable, userId);
	}

	@Override
	public Page<UserResponse.Search> getUsers(Pageable pageable, String keyword, UserRole role) {
		return userRepository.searchUsers(pageable, keyword, role);
	}

	@Override
	public UserResponse.UserDetail getUserDetail(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
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
	public UserResponse.UserDetail getAdminDetail(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
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
	public void enableUser(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
		user.enabled();
	}

	@Override
	public void disableUser(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
		user.disabled();
	}

	@Override
	public void deleteUser(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
		user.delete();
	}

	@Override
	public void retireUser(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
		user.retired();
	}

	@Override
	public void createAdmin(SignupRequest.SignupMember request) {
		UserEntity admin = UserEntity.create(
			request.name(),
			request.email(),
			passwordEncoder.encode(request.password()),
			UserRole.ADMIN,
			request.gender(),
			request.birth(),
			request.mobile()
		);
		userRepository.save(admin);
	}

	@Override
	public void deleteUserPermanently(UUID id) {
		UserEntity user = userRepository.findByIdOrThrow(id);
		orderRepository.clearUser(user.getId());
		userRepository.delete(user);
	}

	@Override
	public void deleteAdmin(UUID adminId) {
		UserEntity user = userRepository.findByIdOrThrow(adminId);
		user.delete();
	}

	@Override
	public void updateUserDetail(
		String email,
		UUID userId,
		UserRequest.UpdateDetails details
	) {
		UserEntity user = userRepository.findByIdOrThrow(userId);
		hasUserAccessPermission(email, user);
		user.updateDetails(
			details.name(),
			details.mobile(),
			details.birth(),
			details.gender()
		);
	}

	private void hasUserAccessPermission(String email, UserEntity user){
		AbstractAccountEntity userEditor = accountRepository.findByEmailOrThrow(email);
		Preconditions.validate(
			userEditor.getEmail().equals(user.getEmail()),
			ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED
		);
	}
}
