package com.kt.domain.entity;

import com.kt.constant.PasswordRequestStatus;
import com.kt.constant.PasswordRequestType;
import com.kt.domain.entity.common.BaseEntity;

import com.kt.util.EncryptUtil;
import com.kt.util.ValidationUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "password_request")
@NoArgsConstructor(access = PROTECTED)
public class PasswordRequestEntity extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private AbstractAccountEntity account;

	private String encryptedPassword;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PasswordRequestType requestType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PasswordRequestStatus status;

	private Instant lastRequestedAt;

	public void updateStatus(PasswordRequestStatus status) {
		this.status = status;
	}

	public void updatePassword(String resetPassword) {
		this.encryptedPassword = EncryptUtil.encrypt(resetPassword);
	}

	public void updateLastRequestedAt() {
		this.lastRequestedAt = Instant.now();
	}

	protected PasswordRequestEntity(
		AbstractAccountEntity account,
		String encryptedPassword,
		PasswordRequestType requestType
	) {
		this.account = account;
		this.encryptedPassword = encryptedPassword;
		this.requestType = requestType;
		this.status = PasswordRequestStatus.PENDING;
		this.lastRequestedAt = null;
	}

	public static PasswordRequestEntity create(
		final AbstractAccountEntity account,
		final String requestedPassword,
		final PasswordRequestType requestType
	) {
		String encryptedPassword = null;
		if (requestType == PasswordRequestType.UPDATE) {
			ValidationUtil.validateNotNullAndBlank(requestedPassword, "변경요청 비밀번호");
			encryptedPassword = EncryptUtil.encrypt(requestedPassword);
		}
		ValidationUtil.validationNotNullEnum(requestType, "비밀번호 복원 요청타입");
		return new PasswordRequestEntity(
			account,
			encryptedPassword,
			requestType
		);
	}

}
