package com.kt.domain.dto.response;

import java.util.UUID;

public record AddressResponse(
	UUID id,
	String receiverName,
	String receiverMobile,
	String city,
	String district,
	String roadAddress,
	String detail
) {}