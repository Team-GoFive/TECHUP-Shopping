package com.kt.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
	@NotBlank String receiverName,
	@NotBlank String receiverMobile,
	@NotBlank String city,
	@NotBlank String district,
	@NotBlank String roadAddress,
	String detail
) {}
