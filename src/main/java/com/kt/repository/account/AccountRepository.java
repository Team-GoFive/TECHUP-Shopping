package com.kt.repository.account;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.AbstractAccountEntity;

import com.kt.exception.CustomException;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<AbstractAccountEntity, UUID>,
	AccountRepositoryCustom {

	default AbstractAccountEntity findByIdOrThrow(UUID accountId) {
		return findById(accountId).orElseThrow(
			() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND)
		);
	}

	default AbstractAccountEntity findByEmailOrThrow(String email) {
		return findByEmail(email).orElseThrow(
			() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND)
		);
	}

	default AbstractAccountEntity findByIdOrThrow(UUID accountId) {
		return findById(accountId).orElseThrow(
			() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND)
		);
	}

	Optional<AbstractAccountEntity> findByEmail(String email);
}
