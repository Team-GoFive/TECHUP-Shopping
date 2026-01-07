package com.kt.repository.bankaccount.transaction;

import com.kt.constant.SortDirection;
import com.kt.constant.bankaccount.BankAccountTransactionType;
import com.kt.domain.dto.request.BankAccountTransactionRequest;
import com.kt.domain.dto.response.BankAccountTransactionResponse;
import com.kt.domain.dto.response.QBankAccountTransactionResponse_Search;
import com.kt.domain.entity.QBankAccountTransactionEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.kt.domain.entity.QBankAccountTransactionEntity.bankAccountTransactionEntity;

@Repository
@RequiredArgsConstructor
public class BankAccountTransactionRepositoryCustomImpl implements BankAccountTransactionRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QBankAccountTransactionEntity bankAccountTransaction = bankAccountTransactionEntity;

	@Override
	public Page<BankAccountTransactionResponse.Search> search(
		UUID holderId,
		BankAccountTransactionRequest.Search search,
		Pageable pageable
	) {
		search.validatePeriod();

		List<BankAccountTransactionResponse.Search> list = jpaQueryFactory
			.select(new QBankAccountTransactionResponse_Search(
				bankAccountTransaction.id,
				bankAccountTransaction.transactionType,
				bankAccountTransaction.amount,
				bankAccountTransaction.balanceSnapshot,
				bankAccountTransaction.depositSource,
				bankAccountTransaction.withdrawalSource,
				bankAccountTransaction.createdAt
			))
			.from(bankAccountTransaction)
			.where(
				eqHolderId(holderId),
				eqType(search.type()),

				betweenPeriod(
					search.resolvedFromDate(),
					search.resolvedToDate()
				),
				containsKeyword(search.keyword())
			)
			.orderBy(orderBy(search))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int totalCount = jpaQueryFactory
			.select(bankAccountTransaction.count())
			.from(bankAccountTransaction)
			.where(
				eqHolderId(holderId),
				eqType(search.type()),
				betweenPeriod(
					search.resolvedFromDate(),
					search.resolvedToDate()
				),
				containsKeyword(search.keyword())
			).fetch().size();

		return new PageImpl<>(list, pageable, totalCount);

	}

	private BooleanExpression eqHolderId(UUID holderId) {
		return bankAccountTransaction.bankAccount.holderId.eq(holderId);
	}

	private BooleanExpression eqType(BankAccountTransactionType type) {
		return type == null ? null : bankAccountTransaction.transactionType.eq(type);
	}

	private BooleanExpression betweenPeriod(
		LocalDate from,
		LocalDate to
	) {
		ZoneId zoneId = ZoneId.systemDefault();

		Instant fromInstant = from.atStartOfDay(zoneId).toInstant();
		Instant toInstant = to.plusDays(1).atStartOfDay(zoneId).toInstant();

		return bankAccountTransaction.createdAt.between(fromInstant, toInstant);
	}

	private BooleanExpression containsKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		return bankAccountTransaction.depositSource.contains(keyword)
			.or(bankAccountTransaction.withdrawalSource.contains(keyword));
	}

	private OrderSpecifier<?> orderBy(
		BankAccountTransactionRequest.Search condition
	) {
		return condition.sort() == SortDirection.ASC
			? bankAccountTransaction.createdAt.asc()
			: bankAccountTransaction.createdAt.desc();
	}
}
