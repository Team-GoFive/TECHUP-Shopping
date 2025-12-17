package com.kt.domain.entity;

import com.kt.constant.AccountRole;

import com.kt.constant.Gender;

import com.kt.constant.UserStatus;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "admin")
@DiscriminatorValue("ADMIN")
@NoArgsConstructor(access = PROTECTED)
public class AdminEntity extends AbstractAccountEntity {

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
