package com.kt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;

public interface PayRepository extends JpaRepository<PayEntity, Long> {
	Optional<PayEntity> findByUser(UserEntity user);
}
