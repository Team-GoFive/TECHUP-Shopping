package com.kt.domain.entity;

import com.kt.domain.capability.BankAccountHolder;
import com.kt.util.ValidationUtil;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.kt.constant.Gender;
import com.kt.constant.AccountRole;
import com.kt.constant.UserStatus;

@Getter
@Entity(name = "seller")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SELLER")
public class SellerEntity extends AbstractAccountEntity implements BankAccountHolder {

	@Column(nullable = false)
	private String storeName;

	@Column(nullable = false)
	private String contactMobile;

	// TODO: 사업자등록번호, 결제용 판매자 계좌번호, 예금주 등

	private SellerEntity(String name, String email, String password,
		String storeName, String contactMobile, Gender gender) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.storeName = storeName;
		this.contactMobile = contactMobile;
		this.role = AccountRole.SELLER;
		this.status = UserStatus.ENABLED;
		this.gender = gender;
	}

	public static SellerEntity create(
		final String name,
		final String email,
		final String password,
		final String storeName,
		final String contactMobile,
		final Gender gender) {
		ValidationUtil.validateNotNullAndBlank(name, "판매자이름");
		ValidationUtil.validateNotNullAndBlank(email, "이메일");
		ValidationUtil.validateAccountPassword(password, "비밀번호");
		ValidationUtil.validateNotNullAndBlank(storeName, "판매 스토어명");
		ValidationUtil.validateNotNullAndBlank(contactMobile, "판매자 연락처");
		ValidationUtil.validateRequiredEnum(gender, "판매자성별");
		return new SellerEntity(name, email, password, storeName, contactMobile, gender);
	}
}
