package com.kt.repository.orderproduct;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.dto.response.OrderProductResponse;
import com.kt.domain.dto.response.QOrderProductResponse_SearchReviewable;
import com.kt.domain.dto.response.QSellerOrderResponse_Search;
import com.kt.domain.dto.response.SellerOrderResponse;
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
	public Page<OrderProductResponse.SearchReviewable> getReviewableOrderProductsByUserId(Pageable pageable,
		UUID userId) {
		BooleanExpression condition = review.orderProduct.isNull()
			.and(orderProduct.status.eq(OrderProductStatus.PURCHASE_CONFIRMED));

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

	@Override
	public Page<SellerOrderResponse.Search> search(
		Pageable pageable,
		UUID orderProductId,
		OrderProductStatus status,
		UUID sellerId
	) {
		List<SellerOrderResponse.Search> content = jpaQueryFactory
			.select(new QSellerOrderResponse_Search(
				orderProduct.order.id,
				orderProduct.order.orderBy.id,
				orderProduct.order.orderBy.name,
				orderProduct.id,
				product.id,
				product.name,
				orderProduct.quantity,
				order.receiverVO,
				orderProduct.status,
				orderProduct.createdAt
			))
			.from(orderProduct)
			.join(orderProduct.order, order)
			.join(orderProduct.product, product)
			.where(
				eqOrderProductId(orderProductId),
				eqStatus(status),
				eqSellerId(sellerId)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory
			.select(orderProduct.count())
			.from(orderProduct)
			.where(
				eqOrderProductId(orderProductId),
				eqStatus(status),
				eqSellerId(sellerId)
			)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression eqOrderProductId(UUID orderProductId) {
		return orderProductId != null ? orderProduct.id.eq(orderProductId) : null;
	}

	private BooleanExpression eqStatus(OrderProductStatus status) {
		return status != null ? orderProduct.status.eq(status) : null;
	}

	private BooleanExpression eqSellerId(UUID sellerId) {
		return sellerId != null ? orderProduct.product.seller.id.eq(sellerId) : null;
	}

}