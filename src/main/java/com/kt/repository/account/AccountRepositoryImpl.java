package com.kt.repository.account;

import com.kt.constant.UserRole;
import com.kt.domain.dto.request.AccountRequest;
import com.kt.domain.dto.response.AccountResponse;
import com.kt.domain.dto.response.QAccountResponse_Search;
import com.kt.domain.entity.QAbstractAccountEntity;
import com.kt.domain.entity.QCourierEntity;
import com.kt.domain.entity.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.kt.domain.entity.QAbstractAccountEntity.abstractAccountEntity;
import static com.kt.domain.entity.QCourierEntity.courierEntity;
import static com.kt.domain.entity.QUserEntity.userEntity;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QAbstractAccountEntity account = abstractAccountEntity;
	private final QCourierEntity courier = courierEntity;
	private final QUserEntity user = userEntity;

	@Override
	public Page<AccountResponse.Search> searchAccounts(AccountRequest.Search request, Pageable pageable) {

		JPQLQuery<AccountResponse.Search> query = jpaQueryFactory
			.select(new QAccountResponse_Search(
				account.name,
				account.email,
				account.status,
				account.role
			))
			.from(account);

		BooleanBuilder builder = new BooleanBuilder();

		if (request.role() != null) {
			builder.and(account.role.eq(request.role()));

			if (request.role() == UserRole.COURIER) {
				query.leftJoin(courier).on(courier.id.eq(account.id));
				if (request.courierWorkStatus() != null) {
					builder.and(courier.workStatus.eq(request.courierWorkStatus()));
				}
			}
		}

		if (request.userStatus() != null) {
			builder.and(account.status.eq(request.userStatus()));
		}

		if (StringUtils.hasText(request.searchKeyword())) {
			builder.and(account.name.contains(request.searchKeyword()));
		}

		query.where(builder);

		List<AccountResponse.Search> list = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = query.fetchCount();

		return new PageImpl<>(list, pageable, totalCount);
	}
}
