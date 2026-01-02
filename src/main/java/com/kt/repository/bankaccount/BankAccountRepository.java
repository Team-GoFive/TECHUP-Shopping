package com.kt.repository.bankaccount;

import com.kt.domain.entity.BankAccountEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {
}
