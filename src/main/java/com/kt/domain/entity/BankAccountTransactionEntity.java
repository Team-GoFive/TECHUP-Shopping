package com.kt.domain.entity;

import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "bank_account_transactions")
@NoArgsConstructor(access = PROTECTED)
public class BankAccountTransactionEntity extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_account_id", nullable = false)
	private BankAccountEntity bankAccount;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BankAccountTransactionType transactionType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BankAccountTransactionPurpose transactionPurpose;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal amount;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal balanceSnapshot;

	@Column(nullable = false)
	private UUID targetId;
}
