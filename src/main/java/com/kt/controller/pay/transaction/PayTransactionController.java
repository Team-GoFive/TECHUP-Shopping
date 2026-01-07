package com.kt.controller.pay.transaction;

import com.kt.common.Paging;
import com.kt.common.api.ApiResult;
import com.kt.common.api.PageResponse;
import com.kt.domain.dto.request.PayTransactionRequest;
import com.kt.domain.dto.response.PayTransactionResponse;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.pay.transaction.PayTransactionService;

import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.kt.common.api.ApiResult.page;

@RestController
@RequestMapping("/api/pay-transactions")
@RequiredArgsConstructor
public class PayTransactionController implements PayTransactionSwaggerSupporter {

	private final PayTransactionService payTransactionService;

	@GetMapping
	public ResponseEntity<ApiResult<PageResponse<PayTransactionResponse.Search>>> getMyTransactions(
		@AuthenticationPrincipal DefaultCurrentUser currentUser,
		@ParameterObject PayTransactionRequest.Search request,
		@ModelAttribute Paging paging
	) {
		UUID userId = currentUser.getId();
		return page(
			payTransactionService.getTransactions(userId, request, paging.toPageable())
		);
	}
}
