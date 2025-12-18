package com.kt.repository.review;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kt.constant.searchtype.ProductSearchType;
import com.kt.domain.dto.response.QReviewResponse_Search;
import com.kt.domain.dto.response.QSellerReviewResponse_Search;
import com.kt.domain.dto.response.ReviewResponse;
import com.kt.domain.dto.response.SellerReviewResponse;
import com.kt.domain.entity.QCategoryEntity;
import com.kt.domain.entity.QOrderEntity;
import com.kt.domain.entity.QOrderProductEntity;
import com.kt.domain.entity.QProductEntity;
import com.kt.domain.entity.QReviewEntity;
import com.kt.domain.entity.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QUserEntity user = QUserEntity.userEntity;
	private final QOrderEntity order = QOrderEntity.orderEntity;
	private final QProductEntity product = QProductEntity.productEntity;
	private final QCategoryEntity category = QCategoryEntity.categoryEntity;
	private final QReviewEntity review = QReviewEntity.reviewEntity;
	private final QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;

	private BooleanExpression containsKeyword(String keyword, ProductSearchType type) {
		if (type == null)
			return null;
		if (Strings.isBlank(keyword))
			return null;
		return (type == ProductSearchType.CATEGORY) ?
			category.name.containsIgnoreCase(keyword) :
			product.name.containsIgnoreCase(keyword);
	}

	@Override
	public Page<ReviewResponse.Search> searchReviews(Pageable pageable, String keyword, ProductSearchType type) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(containsKeyword(keyword, type));

		List<ReviewResponse.Search> content = jpaQueryFactory
			.select(new QReviewResponse_Search(
				review.id,
				review.content
			))
			.from(product)
			.join(category).on(category.id.eq(product.category.id))
			.join(orderProduct).on(orderProduct.product.id.eq(product.id))
			.join(review).on(review.orderProduct.id.eq(orderProduct.id))
			.where(booleanBuilder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory.select(product.id)
			.from(product)
			.join(orderProduct).on(orderProduct.product.id.eq(product.id))
			.join(review).on(review.orderProduct.id.eq(orderProduct.id))
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<ReviewResponse.Search> searchReviewsByUserId(Pageable pageable, UUID userId) {
		List<ReviewResponse.Search> contents = jpaQueryFactory
			.select(new QReviewResponse_Search(
				review.id,
				review.content
			))
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.join(review).on(orderProduct.id.eq(review.orderProduct.id))
			.where(review.isNotNull().and(user.id.eq(userId)))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory
			.select(user.id)
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.join(review).on(orderProduct.id.eq(review.orderProduct.id))
			.where(review.isNotNull().and(user.id.eq(userId)))
			.fetch().size();

		return new PageImpl<>(contents, pageable, total);
	}

	@Override
	public Page<ReviewResponse.Search> searchReviewsByProductId(Pageable pageable, UUID productId) {
		List<ReviewResponse.Search> contents = jpaQueryFactory
			.select(new QReviewResponse_Search(
				review.id,
				review.content
			))
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.join(product).on(product.id.eq(orderProduct.product.id))
			.join(review).on(orderProduct.id.eq(review.orderProduct.id))
			.where(orderProduct.product.id.eq(product.id))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory
			.select(user.id)
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.join(product).on(product.id.eq(orderProduct.product.id))
			.join(review).on(orderProduct.id.eq(review.orderProduct.id))
			.where(orderProduct.product.id.eq(product.id))
			.fetch().size();

		return new PageImpl<>(contents, pageable, total);
	}

	@Override
	public Page<SellerReviewResponse.Search> searchReviewsForSeller(Pageable pageable,
		UUID sellerId,
		UUID productId
	) {
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		booleanBuilder.and(product.seller.id.eq(sellerId));

		if (productId != null) {
			booleanBuilder.and(product.id.eq(productId));
		}

		List<SellerReviewResponse.Search> contents = jpaQueryFactory
			.select(new QSellerReviewResponse_Search(
				product.id,
				product.name,
				user.id,
				user.name,
				review.id,
				review.content
			))
			.from(product)
			.join(orderProduct).on(orderProduct.product.id.eq(product.id))
			.join(review).on(review.orderProduct.id.eq(orderProduct.id))
			.join(order).on(order.id.eq(orderProduct.order.id))
			.join(user).on(user.id.eq(order.orderBy.id))
			.where(booleanBuilder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory
			.select(review.id.count())
			.from(product)
			.join(orderProduct).on(orderProduct.product.id.eq(product.id))
			.join(review).on(review.orderProduct.id.eq(orderProduct.id))
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(contents, pageable, total);
	}
}
