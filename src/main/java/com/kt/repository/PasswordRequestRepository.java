package com.kt.repository;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.PasswordRequestEntity;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordRequestRepository extends
	JpaRepository<PasswordRequestEntity, UUID> {

	Optional<PasswordRequestEntity> findByAccountAndStatusAndRequestType(
		AbstractAccountEntity account,
		PasswordRequestStatus status,
		PasswordRequestType type
	);

	default PasswordRequestEntity findByIdOrThrow(UUID passwordRequestId, PasswordRequestType requestType) {
		ErrorCode error = requestType == PasswordRequestType.RESET ?
			ErrorCode.PASSWORD_RESET_REQUESTS_NOT_FOUND :
			ErrorCode.PASSWORD_UPDATE_REQUESTS_NOT_FOUND;
		return findById(passwordRequestId).orElseThrow(
			() -> new CustomException(error)
		);
	}

}
