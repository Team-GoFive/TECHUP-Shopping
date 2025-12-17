package com.kt.service;

import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.request.SignupRequest;
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
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserResponse.Orders getOrdersByUserId(UUID currentId, UUID subjectId) {
		checkReadPermission(currentId, subjectId);

		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(subjectId);

		return UserResponse.Orders.of(subjectId, orders);
	}

	@Override
	public Page<OrderProductResponse.SearchReviewable> getReviewableOrderProducts(Pageable pageable, UUID userId) {
		return orderProductRepository.getReviewableOrderProductsByUserId(pageable, userId);
	}

	@Override
	public Page<UserResponse.Search> getUsers(UUID userId, Pageable pageable, String keyword, AccountRole role) {
		checkAdmin(userId);

		return userRepository.searchUsers(pageable, keyword, role);
	}

	@Override
	public UserResponse.UserDetail getUserDetail(UUID currentId, UUID subjectId) {
		checkReadPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
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
	public UserResponse.UserDetail getAdminDetail(UUID currentId, UUID subjectId) {
		checkReadPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
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
	public void enableUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.enabled();
	}

	// TODO: for admin
	@Override
	public void disableUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.disabled();
	}

	// TODO: for admin
	@Override
	public void deleteUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.delete();
	}

	// TODO: for admin
	@Override
	public void retireUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.retired();
	}

	// TODO: for admin
	@Override
	public void createAdmin(UUID userId, SignupRequest.SignupUser request) {
		checkAdmin(userId);

		UserEntity admin = UserEntity.create(
			request.name(),
			request.email(),
			passwordEncoder.encode(request.password()),
			AccountRole.ADMIN,
			request.gender(),
			request.birth(),
			request.mobile()
		);
		userRepository.save(admin);
	}

	// TODO: for admin
	@Override
	public void deleteUserPermanently(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		orderRepository.clearUser(user.getId());
		userRepository.delete(user);
	}

	// TODO: for admin
	@Override
	public void deleteAdmin(UUID currentId, UUID adminId) {
		checkAdmin(adminId);
		checkModifyPermission(currentId, adminId);

		UserEntity user = userRepository.findByIdOrThrow(adminId);
		user.delete();
	}

	// TODO: for admin
	@Override
	public void updateUserDetail(
		UUID currentId,
		UUID subjectId,
		UserRequest.UpdateDetails details
	) {
		checkModifyPermission(currentId, subjectId);

		UserEntity subjectUser = userRepository.findByIdOrThrow(subjectId);

		subjectUser.updateDetails(
			details.name(),
			details.mobile(),
			details.birth(),
			details.gender()
		);
	}

	// TODO: for admin
	@Override
	public void updateUserDetailSelf(
		UUID userId,
		UserRequest.UpdateDetails details
	) {
		UserEntity subjectUser = userRepository.findByIdOrThrow(userId);

		subjectUser.updateDetails(
			details.name(),
			details.mobile(),
			details.birth(),
			details.gender()
		);
	}

	private void checkAdmin(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		Preconditions.validate(
			user.getRole() == AccountRole.ADMIN,
			ErrorCode.ADMIN_PERMISSION_REQUIRED
		);
	}

	private void checkReadPermission(UUID currentId, UUID subjectId) {
		UserEntity currentUser = userRepository.findByIdOrThrow(currentId);
		Preconditions.validate(
			currentUser.getRole().equals(AccountRole.ADMIN) | currentId.equals(subjectId),
			ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED
		);
	}

	private void checkModifyPermission(UUID currentId, UUID subjectId) {
		UserEntity subjectUser = userRepository.findByIdOrThrow(subjectId);
		if (subjectUser.getRole() == AccountRole.ADMIN) {
			Preconditions.validate(
				currentId.equals(subjectId),
				ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED
			);
		} else {
			UserEntity currentUser = userRepository.findByIdOrThrow(currentId);
			Preconditions.validate(
				currentUser.getRole().equals(AccountRole.ADMIN) | currentId.equals(subjectId),
				ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED
			);
		}
	}
}
