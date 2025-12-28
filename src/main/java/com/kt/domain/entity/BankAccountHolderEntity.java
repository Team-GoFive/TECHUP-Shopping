package com.kt.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Getter
@Entity
public class BankAccountHolderEntity extends AbstractAccountEntity {

	@OneToOne(
		mappedBy = "holder",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.REMOVE
		},
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	protected BankAccountEntity bankAccount;

}
