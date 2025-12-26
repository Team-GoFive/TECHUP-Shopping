package com.kt.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundHistoryRequest(
	@NotNull UUID orderProductId,
	@NotBlank String reason
) {}
