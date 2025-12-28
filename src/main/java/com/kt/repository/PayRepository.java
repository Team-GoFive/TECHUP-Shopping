package com.kt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.PayEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;

public interface PayRepository extends JpaRepository<PayEntity, Long> {
	Optional<PayEntity> findByUser(UserEntity user);

	default PayEntity findByUserOrThrow(UserEntity user) {
		return findByUser(user)
			.orElseThrow(() -> new CustomException(ErrorCode.PAY_NOT_FOUND));
	}

}
