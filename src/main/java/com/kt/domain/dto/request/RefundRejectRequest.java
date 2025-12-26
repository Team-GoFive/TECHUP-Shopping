package com.kt.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefundRejectRequest(
	@NotBlank String reason
) {}
