package com.kt.repository;

import com.kt.domain.entity.AbstractAccountEntity;
import com.kt.domain.entity.PasswordRequestEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordRequestRepository extends
	JpaRepository<PasswordRequestEntity, UUID> {

	List<PasswordRequestEntity> findAllByAccount(AbstractAccountEntity account);
}
