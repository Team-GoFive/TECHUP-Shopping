package com.kt.domain.entity;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;
import com.kt.exception.CustomException;

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
	}

	public static PayEntity create(final UserEntity user) {
		return new PayEntity(user);
	}

	public void refund(long amount) {
		if (amount <= 0) {
			throw new CustomException(ErrorCode.INVALID_DOMAIN_FIELD);
		}
		this.balance += amount;
	}


}
