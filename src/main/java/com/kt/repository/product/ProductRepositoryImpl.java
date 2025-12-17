package com.kt.repository.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kt.constant.ProductStatus;
import com.kt.constant.AccountRole;
import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.ProductResponse;
import com.kt.domain.dto.response.QProductResponse_Search;
import com.kt.domain.entity.QCategoryEntity;
import com.kt.domain.entity.QProductEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QProductEntity product = QProductEntity.productEntity;
	private final QCategoryEntity category = QCategoryEntity.categoryEntity;

	@Override
	public Page<ProductResponse.Search> search(
		AccountRole role,
		Pageable pageable,
		String keyword,
		ProductSearchType type
	) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(isActiveByRole(role));
		booleanBuilder.and(containsKeyword(keyword, type));
		// jpaQueryFactory
		List<ProductResponse.Search> content = jpaQueryFactory
			.select(new QProductResponse_Search(
				product.id,
				product.name,
				product.price,
				product.status,
				product.category.id,
				product.stock
			))
			.from(product)
			.join(category).on(category.id.eq(product.category.id))
			.where(booleanBuilder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory.select(product.id)
			.from(product)
			.join(category).on(category.id.eq(product.category.id))
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<ProductResponse.Search> searchForSeller(
		Pageable pageable,
		String keyword,
		ProductSearchType type,
		UUID sellerId
	) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(containsKeyword(keyword, type));
		booleanBuilder.and(product.seller.id.eq(sellerId));

		List<ProductResponse.Search> content = jpaQueryFactory
			.select(new QProductResponse_Search(
				product.id,
				product.name,
				product.price,
				product.status,
				product.category.id,
				product.stock
			))
			.from(product)
			.join(category).on(category.id.eq(product.category.id))
			.where(booleanBuilder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory.select(product.id)
			.from(product)
			.join(category).on(category.id.eq(product.category.id))
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression isActiveByRole(AccountRole role) {
		if (role == AccountRole.ADMIN) {
			return null;
		}
		return product.status.eq(ProductStatus.ACTIVATED);
	}

	private BooleanExpression containsKeyword(String keyword, ProductSearchType type) {
		if (keyword == null || keyword.isEmpty() || type == null) {
			return null;
		}

		if (type == ProductSearchType.NAME) {
			return product.name.containsIgnoreCase(keyword);
		}

		if (type == ProductSearchType.CATEGORY) {
			return category.name.containsIgnoreCase(keyword);
		}

		return null;
	}
}
