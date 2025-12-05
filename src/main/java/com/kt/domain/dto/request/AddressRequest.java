package com.kt.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "AddressRequest")
public record AddressRequest(
	@NotBlank(message = "수신자 이름은 필수입니다.")
	String receiverName,
	@NotBlank(message = "수신자 휴대폰 번호는 필수입니다.")
	String receiverMobile,
	@NotBlank(message = "도시 입력은 필수입니다.")
	String city,
	@NotBlank(message = "구/군 입력은 필수입니다.")
	String district,
	@NotBlank(message = "도로명 입력은 필수입니다.")
	String roadAddress,
	@NotBlank(message = "상세주소 입력은 필수입니다.")
	String detail
) {
}
