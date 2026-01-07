package com.kt.repository.pay.transaction;

import com.kt.constant.pay.PayTransactionType;
import com.kt.domain.dto.response.PayTransactionResponse;

import com.kt.domain.dto.response.QPayTransactionResponse_Search;
import com.kt.domain.entity.QPayTransactionEntity;
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

import static com.kt.domain.entity.QPayTransactionEntity.payTransactionEntity;

@Repository
@RequiredArgsConstructor
public class PayTransactionRepositoryCustomImpl implements PayTransactionRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QPayTransactionEntity payTransaction = payTransactionEntity;

	@Override
	public Page<PayTransactionResponse.Search> search(
		UUID userId,
		PayTransactionType type,
		LocalDate fromDate,
		LocalDate toDate,
		String keyword,
		Pageable pageable
	) {

		List<PayTransactionResponse.Search> list = jpaQueryFactory
			.select(new QPayTransactionResponse_Search(
				payTransaction.id,
				payTransaction.transactionType,
				payTransaction.amount,
				payTransaction.balanceSnapshot,
				payTransaction.withdrawalSource,
				payTransaction.depositSource,
				payTransaction.createdAt
			))
			.from(payTransaction)
			.where(
				eqUserId(userId),
				eqType(type),
				betweenPeriod(
					fromDate,
					toDate
				),
				containsKeyword(keyword)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int totalCount = jpaQueryFactory
			.select(payTransaction.count())
			.from(payTransaction)
			.where(
				eqUserId(userId),
				eqType(type),
				betweenPeriod(
					fromDate,
					toDate
				),
				containsKeyword(keyword)
			).fetch()
			.size();

		return new PageImpl<>(list, pageable, totalCount);
	}

	private BooleanExpression eqUserId(UUID userId) {
		return payTransaction.pay.user.id.eq(userId);
	}

	private BooleanExpression eqType(PayTransactionType type) {
		return type == null ? null : payTransaction.transactionType.eq(type);
	}

	private BooleanExpression betweenPeriod(LocalDate from, LocalDate to) {
		ZoneId zoneId = ZoneId.systemDefault();

		Instant fromInstant = from.atStartOfDay(zoneId).toInstant();
		Instant toInstant = to.plusDays(1).atStartOfDay(zoneId).toInstant();

		return payTransaction.createdAt.between(fromInstant, toInstant);
	}

	private BooleanExpression containsKeyword(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}

		return payTransaction.depositSource.contains(keyword)
			.or(payTransaction.withdrawalSource.contains(keyword));
	}
}
