package com.kt.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class PayRequest {

	@Schema(name = "PayChargeRequest")
	public record Charge(
		long amount
	) {
	}
}
