package com.kt.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.exception.CustomException;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
	List<AddressEntity> findAllByCreatedBy(UserEntity user);

	Optional<AddressEntity> findByIdAndCreatedBy(UUID id, UserEntity createdBy);

	default AddressEntity findByIdAndCreatedByOrThrow(UUID id, UserEntity user) {
		return findByIdAndCreatedBy(id, user)
			.orElseThrow(() -> new CustomException(ErrorCode.ADDRESS_NOT_FOUND));
	}
}

