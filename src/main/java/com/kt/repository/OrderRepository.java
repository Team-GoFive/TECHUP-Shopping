package com.kt.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.OrderEntity;
import com.kt.exception.CustomException;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
	List<OrderEntity> findAllByOrderBy_Id(UUID id);

	default OrderEntity findByIdOrThrow(UUID orderId) {
		return findById(orderId).orElseThrow(
			() -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
		);
	}

	@Modifying
	@Query("UPDATE OrderEntity o SET o.orderBy = NULL WHERE o.orderBy.id = :userId")
	void clearUser(UUID userId);
}
