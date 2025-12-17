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

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "bank_account")
@NoArgsConstructor(access = PROTECTED)
public class BankAccountEntity extends BaseEntity {

	@Column(nullable = false)
	private Long balance;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "account_id",
		nullable = false,
		unique = true
	)
	private AbstractAccountEntity account;

	protected BankAccountEntity(AbstractAccountEntity account) {
		this.balance = 0L;
		this.account = account;
	}

	public static BankAccountEntity create(final AbstractAccountEntity account) {
		return new BankAccountEntity(account);
	}

}
