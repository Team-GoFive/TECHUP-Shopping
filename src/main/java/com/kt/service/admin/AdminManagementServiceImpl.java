package com.kt.service.admin;

import java.util.UUID;

import com.kt.constant.AccountRole;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.SignupRequest;
import com.kt.domain.dto.request.UserRequest;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.user.UserRepository;
import com.kt.util.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminManagementServiceImpl implements AdminManagementService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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

	@Override
	public void deleteAdmin(UUID currentId, UUID adminId) {
		checkAdmin(adminId);
		checkModifyPermission(currentId, adminId);

		UserEntity user = userRepository.findByIdOrThrow(adminId);
		user.delete();
	}

	private void checkAdmin(UUID currentUserId) {
		UserEntity user = userRepository.findByIdOrThrow(currentUserId);
		Preconditions.validate(
			user.getRole() == AccountRole.ADMIN,
			ErrorCode.NOT_ADMIN
		);
	}

	// TODO: 테스트 작성
	@Override
	public void updateDetail(
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
