package com.kt.repository;

import com.kt.constant.PasswordRequestStatus;
import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.PasswordRequestEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordRequestRepository extends
	JpaRepository<PasswordRequestEntity, UUID> {

	Optional<PasswordRequestEntity> findByAccountAndStatus(
		AbstractAccountEntity account,
		PasswordRequestStatus status
	);


}
