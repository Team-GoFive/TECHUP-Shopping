package com.kt.service.admin;

import java.util.UUID;

import com.kt.constant.AccountRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.dto.request.CourierRequest;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.CourierEntity;
import com.kt.repository.account.AccountRepository;
import com.kt.repository.courier.CourierRepository;
import com.kt.util.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCourierServiceImpl implements AdminCourierService {

	private final CourierRepository courierRepository;
	private final AccountRepository accountRepository;

	@Override
	public CourierResponse.DetailAdmin getDetail(UUID currentId, UUID subjectId) {
		verifyAccess(currentId, subjectId);

		CourierEntity courierEntity = courierRepository.findByIdOrThrow(subjectId);
		return new CourierResponse.DetailAdmin(
			courierEntity.getId(),
			courierEntity.getName(),
			courierEntity.getEmail(),
			courierEntity.getGender(),
			courierEntity.getStatus(),
			courierEntity.getWorkStatus()
		);
	}

	@Override
	public void updateDetail(UUID currentId, UUID subjectId, CourierRequest.UpdateDetails details) {
		verifyAccess(currentId, subjectId);

		CourierEntity courier = courierRepository.findByIdOrThrow(subjectId);
		courier.updateDetails(
			details.name(),
			details.gender()
		);
	}

	private void verifyAccess(UUID currentId, UUID subjectId) {
		AbstractAccountEntity currentUser = accountRepository.findByIdOrThrow(currentId);

		if (currentUser.getRole() != AccountRole.ADMIN) {
			Preconditions.validate(
				currentId.equals(subjectId),
				ErrorCode.ACCOUNT_ACCESS_NOT_ALLOWED
			);
		}
	}
}
