package com.kt.domain.entity;

import com.kt.constant.AccountRole;

import com.kt.constant.Gender;

import com.kt.constant.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(
	name = "admin",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_admin_code",
			columnNames = "admin_code"
		)
	}
)
@DiscriminatorValue("ADMIN")
@NoArgsConstructor(access = PROTECTED)
public class AdminEntity extends AbstractAccountEntity {

	public static final String SYSTEM_ADMIN_CODE = "SYSTEM_ADMIN";

	@Column(name = "admin_code", nullable = false, updatable = false)
	private String adminCode;

	protected AdminEntity(
		String name,
		String email,
		String password,
		Gender gender
	) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.role = AccountRole.ADMIN;
		this.status = UserStatus.ENABLED;
		this.adminCode = SYSTEM_ADMIN_CODE;
	}

	public static AdminEntity create(
		final String name,
		final String email,
		final String password,
		final Gender gender
	) {
		return new AdminEntity(name, email, password, gender);
	}
}
