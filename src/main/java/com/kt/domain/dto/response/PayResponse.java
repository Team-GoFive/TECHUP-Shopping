package com.kt.domain.dto.response;

import com.kt.domain.entity.PayEntity;

import java.math.BigDecimal;

public class PayResponse {

	public record Balance(
		BigDecimal balance
	) {
		public static Balance from(PayEntity pay) {
			return new Balance(pay.getBalance());
		}
	}
}
