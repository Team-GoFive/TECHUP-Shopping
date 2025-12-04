package com.kt.domain.entity;

import javax.sound.midi.Receiver;

import com.kt.util.ValidationUtil;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class ReceiverVO {

	private String name;
	private String mobile;
	private String city;
	private String district;
	private String road_address;
	private String detail;

	public ReceiverVO(
		String name,
		String mobile,
		String city,
		String district,
		String road_address,
		String detail
	) {
		ValidationUtil.validateNotNullAndBlank(name, "수신자이름");
		ValidationUtil.validateNotNullAndBlank(mobile, "수신자전화번호");
		ValidationUtil.validateNotNullAndBlank(city, "시/도");
		ValidationUtil.validateNotNullAndBlank(district, "시/군/구");
		ValidationUtil.validateNotNullAndBlank(road_address, "도로명주소");
		ValidationUtil.validateNotNullAndBlank(detail, "상세주소");
		this.name = name;
		this.mobile = mobile;
		this.city = city;
		this.district = district;
		this.road_address = road_address;
		this.detail = detail;
	}

	public static ReceiverVO create(
		String name,
		String mobile,
		String city,
		String district,
		String road_address,
		String detail
	) {
		return new ReceiverVO(
			name,
			mobile,
			city,
			district,
			road_address,
			detail
		);
	}
}
