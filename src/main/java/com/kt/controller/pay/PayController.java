package com.kt.controller.pay;

import com.kt.common.api.ApiResult;
import com.kt.domain.dto.request.PayRequest;
import com.kt.domain.dto.response.PayResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.pay.PayChargeService;

import com.kt.service.pay.PayService;
import com.kt.service.pay.PayWithdrawalService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.kt.common.api.ApiResult.*;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController implements PaySwaggerSupporter {

	private final PayChargeService payChargeService;
	private final PayWithdrawalService payWithdrawalService;
	private final PayService payService;

	@PostMapping("/charges")
	public ResponseEntity<ApiResult<Void>> charge(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody PayRequest.Charge request
	) {
		UUID userId = currentUser.getId();
		payChargeService.charge(request.amount(), userId);
		return empty();
	}


	@PostMapping("/withdrawals")
	public ResponseEntity<ApiResult<Void>> withdraw(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@RequestBody PayRequest.Withdrawal request
	) {
		UUID userId = currentUser.getId();
		payWithdrawalService.withdraw(request.amount(), userId);
		return empty();
	}

	@GetMapping("/balance")
	public ResponseEntity<ApiResult<PayResponse.Balance>> balance(
		@AuthenticationPrincipal DefaultCurrentUser currentUser
	) {
		UUID userId = currentUser.getId();
		return wrap(payService.getBalance(userId));
	}

}
