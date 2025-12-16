package com.kt.repository.user;

import com.kt.constant.AccountRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.constant.UserStatus;
import com.kt.domain.dto.response.QUserResponse_Search;
import com.kt.domain.dto.response.UserResponse;
import com.kt.domain.entity.QUserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QUserEntity user = QUserEntity.userEntity;

	@Override
	public Page<UserResponse.Search> searchUsers(Pageable pageable, String keyword, AccountRole role) {

		BooleanExpression condition = isEnabled()
			.and(containsName(keyword))
			.and(equalRole(role));

		var contentQuery = jpaQueryFactory
			.select(new QUserResponse_Search(
				user.id,
				user.name,
				user.email,
				user.role,
				user.gender,
				user.birth,
				user.mobile
			))
			.from(user)
			.where(condition)
			.orderBy(user.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		var countQuery = jpaQueryFactory
			.select(user.count())
			.from(user)
			.where(condition);

		return new PageImpl<>(
			contentQuery.fetch(),
			pageable,
			countQuery.fetchOne()
		);
	}

	private BooleanExpression containsName(String keyword) {
		return (keyword == null || keyword.isBlank()) ? null : user.name.contains(keyword);
	}

	private BooleanExpression equalRole(AccountRole role) {
		return (role == null) ? null : user.role.eq(role);
	}

	private BooleanExpression isEnabled() {
		// TODO: 유저 상태 논의 후 enabled 결정
		return user.status.eq(UserStatus.ENABLED);
	}

}
