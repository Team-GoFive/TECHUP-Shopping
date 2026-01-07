package com.kt.repository.bankaccount.transaction;

import com.kt.domain.entity.BankAccountTransactionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankAccountTransactionRepository extends
	JpaRepository<BankAccountTransactionEntity, UUID>,
	BankAccountTransactionRepositoryCustom {

}
