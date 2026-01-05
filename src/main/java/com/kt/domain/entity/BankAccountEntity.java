package com.kt.domain.entity;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.capability.BankAccountHolder;
import com.kt.domain.entity.common.BaseEntity;

import com.kt.exception.CustomException;

import com.kt.util.ValidationUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "bank_account")
@NoArgsConstructor(access = PROTECTED)
public class BankAccountEntity extends BaseEntity {

	@Column(nullable = false)
	private String displayName;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal balance;

	@Version
	private Long version;

	@Column(nullable = false, unique = true)
	private UUID holderId;

	private BankAccountEntity(UUID holderId, String displayName) {
		this.displayName = displayName;
		this.holderId = holderId;
		this.balance = BigDecimal.ZERO;
	}

	public static BankAccountEntity create(final BankAccountHolder holder, final String displayName) {
		return new BankAccountEntity(holder.getId(), displayName);
	}

	public void deposit(long amount) {
		ValidationUtil.validatePositive(amount, "입금금액");
		BigDecimal depositAmount = BigDecimal.valueOf(amount);
		this.balance = this.balance.add(depositAmount);
	}

	public void withdraw(long amount) {

		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		if (this.balance.compareTo(withdrawAmount) < 0)
			throw new CustomException(ErrorCode.BANK_ACCOUNT_BALANCE_NOT_ENOUGH);
		this.balance = this.balance.subtract(withdrawAmount);
	}
}
