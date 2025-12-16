package com.kt.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kt.constant.Gender;
import com.kt.constant.UserRole;
import com.kt.constant.UserStatus;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SellerEntity extends AbstractAccountEntity {

	@Column(nullable = false)
	private String storeName;

	@Column(nullable = false)
	private String contactMobile;

	@Column(nullable = false)
	@Email
	private String contactEmail;

	// TODO: 사업자등록번호, 결제용 판매자 계좌번호, 예금주 등

	protected SellerEntity(String name, String email, String password, String storeName, String contactMobile,
		String contactEmail, Gender gender) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.storeName = storeName;
		this.contactMobile = contactMobile;
		this.contactEmail = contactEmail;
		this.role = UserRole.SELLER;
		this.status = UserStatus.ENABLED;
		this.gender = gender;
	}

	public static SellerEntity create(String name, String email, String password, String storeName, String contactMobile,
		String contactEmail, Gender gender) {
		return new SellerEntity(name, email, password, storeName, contactMobile, contactEmail, gender);
	}
}
