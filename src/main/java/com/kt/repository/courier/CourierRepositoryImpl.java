package com.kt.repository.courier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.constant.CourierWorkStatus;
import com.kt.constant.UserStatus;
import com.kt.domain.dto.response.CourierResponse;
import com.kt.domain.dto.response.QCourierResponse_Search;
import com.kt.domain.entity.QCourierEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CourierRepositoryImpl implements CourierRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	private final QCourierEntity courier = QCourierEntity.courierEntity;

	@Override
	public Page<CourierResponse.Search> searchCouriers(Pageable pageable, String keyword, CourierWorkStatus workStatus) {

		BooleanExpression condition = isEnabled()
			.and(containsName(keyword))
			.and(equalWorkStatus(workStatus));

		var contentQuery = jpaQueryFactory
			.select(new QCourierResponse_Search(
				courier.id,
				courier.name,
				courier.email,
				courier.gender,
				courier.workStatus
			))
			.from(courier)
			.where(condition)
			.orderBy(courier.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		var countQuery = jpaQueryFactory
			.select(courier.count())
			.from(courier)
			.where(condition);

		return new PageImpl<>(
			contentQuery.fetch(),
			pageable,
			countQuery.fetchOne()
		);
	}

	private BooleanExpression containsName(String keyword) {
		return (keyword == null || keyword.isBlank()) ? null : courier.name.contains(keyword);
	}

	private BooleanExpression equalWorkStatus(CourierWorkStatus status) {
		return (status == null) ? null : courier.workStatus.eq(status);
	}

	private BooleanExpression isEnabled() {
		return courier.status.eq(UserStatus.ENABLED);
	}
}
