package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "account")
@NoArgsConstructor(access = PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type")
public abstract class AbstractAccountEntity extends BaseEntity {

	@Column(nullable = false)
	protected String name;

	@Column(nullable = false)
	protected String email;

	@Column(nullable = false)
	protected String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected UserRole role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected Gender gender;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	protected UserStatus status;

	public void disabled() {
		this.status = UserStatus.DISABLED;
	}

	public void retired() {
		this.status = UserStatus.RETIRED;
	}

	public void delete() {
		this.status = UserStatus.DELETED;
	}

	public void enabled() {
		this.status = UserStatus.ENABLED;
	}

	public void resetPassword(String reset) {
		this.password = reset;
	}

	public void updatePassword(String update){
		this.password = update;
	}
}
