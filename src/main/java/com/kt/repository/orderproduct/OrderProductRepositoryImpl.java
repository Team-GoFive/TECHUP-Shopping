package com.kt.repository.orderproduct;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.constant.OrderStatus;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.QOrderProductResponse_SearchReviewable;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.QOrderEntity;
import com.kt.domain.entity.QOrderProductEntity;
import com.kt.domain.entity.QProductEntity;
import com.kt.domain.entity.QReviewEntity;
import com.kt.domain.entity.QUserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QUserEntity user = QUserEntity.userEntity;
	private final QOrderEntity order = QOrderEntity.orderEntity;
	private final QProductEntity product = QProductEntity.productEntity;
	private final QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
	private final QReviewEntity review = QReviewEntity.reviewEntity;

	@Override
	public Page<OrderProductResponse.SearchReviewable> getReviewableOrderProductsByUserId(Pageable pageable, UUID userId) {
		BooleanExpression condition = review.orderProduct.isNull()
			.and(order.status.eq(OrderStatus.PURCHASE_CONFIRMED));

		List<OrderProductResponse.SearchReviewable> content = jpaQueryFactory
			.select(new QOrderProductResponse_SearchReviewable(
				orderProduct.id,
				orderProduct.quantity,
				orderProduct.unitPrice,
				orderProduct.status
			))
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.leftJoin(review).on(orderProduct.id.eq(review.orderProduct.id))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.where(condition)
			.fetch();

		int total = jpaQueryFactory
			.select(new QOrderProductResponse_SearchReviewable(
				orderProduct.id,
				orderProduct.quantity,
				orderProduct.unitPrice,
				orderProduct.status
			))
			.from(user)
			.join(order).on(user.id.eq(order.orderBy.id))
			.join(orderProduct).on(order.id.eq(orderProduct.order.id))
			.leftJoin(review).on(orderProduct.id.eq(review.orderProduct.id))
			.where(condition)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public List<OrderProductEntity> findWithProductByOrderId(UUID orderId) {

		return jpaQueryFactory
			.selectFrom(orderProduct)
			.distinct()
			.join(orderProduct.product, product).fetchJoin()
			.where(orderProduct.order.id.eq(orderId))
			.fetch();
	}

}