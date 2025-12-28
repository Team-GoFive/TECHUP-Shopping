package com.kt.domain.entity;

import com.kt.domain.entity.common.BaseEntity;

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

}
