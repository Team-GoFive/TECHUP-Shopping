package com.kt.domain.dto.response;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddressResponse")
public record AddressResponse(
	UUID id,
	String receiverName,
	String receiverMobile,
	String city,
	String district,
	String roadAddress,
	String detail
) {}