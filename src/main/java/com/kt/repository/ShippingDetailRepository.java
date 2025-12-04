package com.kt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ShippingDetailEntity;

public interface ShippingDetailRepository extends JpaRepository<ShippingDetailEntity, Long> {
	List<ShippingDetailEntity> findAllByOrderProductIn(List<OrderProductEntity> orderProducts);
}

