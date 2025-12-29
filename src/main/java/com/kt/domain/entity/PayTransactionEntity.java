package com.kt.domain.entity;

import com.kt.constant.pay.PayTransactionPurpose;
import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.entity.common.BaseEntity;

import com.kt.util.ValidationUtil;

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
@Entity(name = "pay_transaction")
@NoArgsConstructor(access = PROTECTED)
public class PayTransactionEntity extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_id", nullable = false)
	private PayEntity pay;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PayTransactionType transactionType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PayTransactionPurpose transactionPurpose;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal amount;

	@Column(precision = 19, scale = 0, nullable = false)
	private BigDecimal balanceSnapshot;

	@Column(nullable = false)
	private UUID targetId;

	private PayTransactionEntity(
		PayEntity pay,
		PayTransactionType transactionType,
		PayTransactionPurpose transactionPurpose,
		BigDecimal amount,
		BigDecimal balanceSnapshot,
		UUID targetId
	) {
		this.pay = pay;
		this.transactionType = transactionType;
		this.transactionPurpose = transactionPurpose;
		this.amount = amount;
		this.balanceSnapshot = balanceSnapshot;
		this.targetId = targetId;
	}

	public static PayTransactionEntity create(
		final PayEntity pay,
		final PayTransactionType transactionType,
		final PayTransactionPurpose transactionPurpose,
		final long amount,
		final BigDecimal balanceSnapshot,
		final UUID targetId
	) {
		ValidationUtil.validateRequiredEnum(transactionType, "페이 거래타입");
		ValidationUtil.validateRequiredEnum(transactionPurpose, "페이 거래목적");
		ValidationUtil.validatePositive(amount, "페이 거래금액");
		BigDecimal transactionAmount = BigDecimal.valueOf(amount);
		return new PayTransactionEntity(
			pay,
			transactionType,
			transactionPurpose,
			transactionAmount,
			balanceSnapshot,
			targetId
		);
	}

}
