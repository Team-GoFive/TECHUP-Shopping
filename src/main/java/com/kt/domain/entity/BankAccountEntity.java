package com.kt.domain.entity;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;

import com.kt.exception.CustomException;

import com.kt.util.ValidationUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "bank_account")
@NoArgsConstructor(access = PROTECTED)
public class BankAccountEntity extends BaseEntity {

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal balance;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "holder_id",
		nullable = false,
		unique = true
	)
	private BankAccountHolderEntity holder;

	private BankAccountEntity(BankAccountHolderEntity holder) {
		this.balance = BigDecimal.ZERO;
		this.holder = holder;
	}

	public static BankAccountEntity create(final BankAccountHolderEntity holder) {
		return new BankAccountEntity(holder);
	}

	public void deposit(long amount) {
		ValidationUtil.validatePositive(amount, "입금금액");
		BigDecimal salaryAmount = BigDecimal.valueOf(amount);
		this.balance.add(salaryAmount);
	}

	public void withdraw(Long amount) {
		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		if (this.balance.compareTo(withdrawAmount) < 0)
			throw new CustomException(ErrorCode.BANK_ACCOUNT_BALANCE_NOT_ENOUGH);
		this.balance = this.balance.subtract(withdrawAmount);
	}
}
