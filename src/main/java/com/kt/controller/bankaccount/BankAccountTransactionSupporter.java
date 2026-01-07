package com.kt.controller.bankaccount;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.dto.response.BankAccountTransactionResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;


@Tag(name = "BankAccountTransaction", description = "계좌 거래내역 관련 API")
public interface BankAccountTransactionSupporter {

	@Operation(
		summary = "계좌 거래내역 조회",
		description = "계좌 거래내역 조회 API"
	)
	ResponseEntity<ApiResult<PageResponse<BankAccountTransactionResponse.Search>>> getMyTransactions(
		DefaultCurrentUser currentUser,
		BankAccountTransactionRequest.Search request,
		Paging paging
	);

}
