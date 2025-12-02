package com.kt.repository.courier;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.CourierEntity;
import com.kt.exception.CustomException;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, UUID>, CourierRepositoryCustom {
	Optional<CourierEntity> findByEmail(String email);

	default CourierEntity findByCourierIdOrThrow(UUID courierId) {
		return findById(courierId).orElseThrow(
				()-> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND)
			);
	}
}
