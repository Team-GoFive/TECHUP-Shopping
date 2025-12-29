package com.kt.repository;

import com.kt.domain.entity.PayTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PayTransactionRepository extends JpaRepository<PayTransactionEntity, UUID> {
}
