package com.kt.controller.pay.transaction;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.PayTransactionRequest;
import com.kt.domain.dto.response.PayTransactionResponse;
import com.kt.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

@Tag(name = "BankAccountTransaction", description = "계좌 거래내역 관련 API")
public interface PayTransactionSwaggerSupporter {

	@Operation(
		summary = "페이 거래내역 조회",
		description = "패이 거래내역 조회 API"
	)
	ResponseEntity<ApiResult<PageResponse<PayTransactionResponse.Search>>> getMyTransactions(
		DefaultCurrentUser currentUser,
		PayTransactionRequest.Search request,
		Paging paging
	);

}
