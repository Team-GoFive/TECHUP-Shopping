package com.kt.service.pay;

import com.kt.domain.dto.response.PayResponse;

import java.util.UUID;

public interface PayService {
	PayResponse.Balance getBalance(UUID userId);
}
