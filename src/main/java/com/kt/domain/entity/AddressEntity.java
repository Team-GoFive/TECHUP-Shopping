package com.kt.domain.entity;

import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "address")
@NoArgsConstructor(access = PROTECTED)
public class AddressEntity extends BaseEntity {

	@Column(nullable = false)
	private String receiverName;

	@Column(nullable = false)
	private String receiverMobile;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String district;

	@Column(nullable = false)
	private String roadAddress;

	@Column(nullable = false)
	private String detail;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private UserEntity createdBy;

	protected AddressEntity(
		String receiverName,
		String receiverMobile,
		String city,
		String district,
		String roadAddress,
		String detail,
		UserEntity createdBy
	) {
		this.receiverName = receiverName;
		this.receiverMobile = receiverMobile;
		this.city = city;
		this.district = district;
		this.roadAddress = roadAddress;
		this.detail = detail;
		this.createdBy = createdBy;
	}

	public static AddressEntity create(
		final String receiverName,
		final String receiverMobile,
		final String city,
		final String district,
		final String roadAddress,
		final String detail,
		final UserEntity createdBy
	) {
		return new AddressEntity(
			receiverName,
			receiverMobile,
			city,
			district,
			roadAddress,
			detail,
			createdBy
		);
	}

	public void update(
		String receiverName,
		String receiverMobile,
		String city,
		String district,
		String roadAddress,
		String detail
	) {
		this.receiverName = receiverName;
		this.receiverMobile = receiverMobile;
		this.city = city;
		this.district = district;
		this.roadAddress = roadAddress;
		this.detail = detail;
	}


}
