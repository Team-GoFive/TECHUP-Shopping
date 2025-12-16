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
@Entity(name = "pay")
@NoArgsConstructor(access = PROTECTED)
public class PayEntity extends BaseEntity {

	@Column(nullable = false)
	private Long balance;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "user_id",
		nullable = false,
		unique = true
	)
	private UserEntity user;

	protected PayEntity(UserEntity user) {
		this.balance = 0L;
		this.user = user;
	}

	public static PayEntity create(final UserEntity user) {
		return new PayEntity(user);
	}

}
