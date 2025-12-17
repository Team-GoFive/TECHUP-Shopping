package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDate;
import java.util.List;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.constant.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "\"user\"")
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("USER")
public class UserEntity extends AbstractAccountEntity {

	@OneToMany(
		mappedBy = "createdBy",
		cascade = CascadeType.REMOVE,
		orphanRemoval = true
	)
	List<AddressEntity> addresses;

	@OneToOne(
		mappedBy = "user",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.REMOVE
		},
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	private PayEntity pay;

	@OneToOne(
		mappedBy = "account",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.REMOVE
		},
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	private BankAccountEntity bankAccount;

	@Column(nullable = false)
	private LocalDate birth;
	@Column(nullable = false)
	private String mobile;

	protected UserEntity(
		String name,
		String email,
		String password,
		AccountRole role,
		Gender gender,
		LocalDate birth,
		String mobile
	) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.gender = gender;
		this.birth = birth;
		this.mobile = mobile;
		this.status = UserStatus.ENABLED;
		this.pay = PayEntity.create(this);
		this.bankAccount = BankAccountEntity.create(this);
	}

	public static UserEntity create(
		final String name,
		final String email,
		final String password,
		final AccountRole role,
		final Gender gender,
		final LocalDate birth,
		final String mobile
	) {
		return new UserEntity(
			name, email, password, role, gender, birth, mobile
		);
	}

	public void delete() {
		this.status = UserStatus.DELETED;
	}

	public void updateDetails(
		String name,
		String mobile,
		LocalDate birth,
		Gender gender
	) {
		this.name = name;
		this.mobile = mobile;
		this.birth = birth;
		this.gender = gender;
	}
}
