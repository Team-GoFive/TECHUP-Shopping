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
@Entity(name = "pay")
@NoArgsConstructor(access = PROTECTED)
public class PayEntity extends BaseEntity {

	private static final String DISPLAY_NAME_SUFFIX = "_페이";

	@Column(nullable = false)
	private String displayName;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal balance;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "user_id",
		nullable = false,
		unique = true
	)
	private UserEntity user;

	private PayEntity(UserEntity user) {
		this.balance = BigDecimal.ZERO;
		this.user = user;
		this.displayName = user.getName() + DISPLAY_NAME_SUFFIX;
	}

	public static PayEntity create(final UserEntity user) {
		return new PayEntity(user);
	}

	public void refund(long amount) {
		if (amount <= 0) {
			throw new CustomException(ErrorCode.INVALID_DOMAIN_FIELD);
		}
		this.balance = this.balance.add(BigDecimal.valueOf(amount));
	}


	public void charge(long amount) {
		ValidationUtil.validatePositive(amount, "충전금액");
		BigDecimal chargeAmount = BigDecimal.valueOf(amount);
		this.balance = this.balance.add(chargeAmount);
	}

	public void withdraw(long amount) {
		ValidationUtil.validatePositive(amount, "인출금액");
		BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
		if (this.balance.compareTo(withdrawAmount) < 0)
			throw new CustomException(ErrorCode.PAY_BALANCE_NOT_ENOUGH);
		this.balance = this.balance.subtract(withdrawAmount);
	}

}
