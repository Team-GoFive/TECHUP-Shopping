package com.kt.repository.bankaccount;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.BankAccountEntity;

import com.kt.exception.CustomException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

	default BankAccountEntity findByHolderIdOrThrow(UUID holderId) {
		return findByHolderId(holderId).orElseThrow(
			() -> new CustomException(ErrorCode.BANK_ACCOUNT_NOT_FOUND)
		);
	}

	Optional<BankAccountEntity> findByHolderId(UUID holderId);

}
