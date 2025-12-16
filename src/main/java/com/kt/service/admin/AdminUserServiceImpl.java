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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	@Override
	public Page<UserResponse.Search> getUsers(UUID userId, Pageable pageable, String keyword, AccountRole role) {
		checkAdmin(userId);
		return userRepository.searchUsers(pageable, keyword, role);
	}

	@Override
	public UserResponse.Orders getOrdersByUserId(UUID currentId, UUID subjectId) {
		checkReadPermission(currentId, subjectId);

		List<OrderEntity> orders = orderRepository.findAllByOrderBy_Id(subjectId);

		return UserResponse.Orders.of(subjectId, orders);
	}

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
	public void enableUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.enabled();
	}

	@Override
	public void disableUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.disabled();
	}

	@Override
	public void deleteUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.delete();
	}

	@Override
	public void retireUser(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		user.retired();
	}

	@Override
	public void deleteUserPermanently(UUID currentId, UUID subjectId) {
		checkModifyPermission(currentId, subjectId);

		UserEntity user = userRepository.findByIdOrThrow(subjectId);
		orderRepository.clearUser(user.getId());
		userRepository.delete(user);
	}

	// TODO: 서비스 분리로 인해 필요 없는 권한 체크 메서드 삭제 검토 필요
	private void checkAdmin(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		Preconditions.validate(
			user.getRole() == AccountRole.ADMIN,
			ErrorCode.NOT_ADMIN
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
