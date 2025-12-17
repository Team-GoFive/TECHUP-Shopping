package com.kt.domain.entity;

import com.kt.constant.bankaccount.BankAccountTransactionPurpose;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "bank_account_transactions")
@NoArgsConstructor(access = PROTECTED)
public class BankAccountTransactionEntity extends BaseEntity {

	@Column(nullable = false)
	private BankAccountTransactionType transactionType;

	@Column(nullable = false)
	private BankAccountTransactionPurpose transactionPurpose;
}
