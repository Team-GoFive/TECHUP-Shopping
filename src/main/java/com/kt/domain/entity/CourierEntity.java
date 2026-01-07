package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import com.kt.constant.AccountRole;
import com.kt.constant.CourierWorkStatus;
import com.kt.constant.Gender;
import com.kt.constant.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "courier")
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("COURIER")
public class CourierEntity extends AbstractAccountEntity {

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CourierWorkStatus workStatus;

	protected CourierEntity(
		String name,
		String email,
		String password,
		Gender gender
	) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.role = AccountRole.COURIER;
		this.status = UserStatus.ENABLED;
		this.workStatus = CourierWorkStatus.RESTING;
	}

	public static CourierEntity create(
		final String name,
		final String email,
		final String password,
		final Gender gender
	) {
		return new CourierEntity(name, email, password, gender);
	}

	public void updateDetails(
		String name,
		Gender gender
	){
		this.name = name;
		this.gender = gender;
	}
}
