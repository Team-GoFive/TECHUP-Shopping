package com.kt.repository.refund;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.kt.constant.RefundStatus;
import com.kt.domain.dto.response.RefundQueryResponse;
import com.kt.domain.entity.QRefundHistoryEntity;
import com.kt.domain.entity.QOrderProductEntity;
import com.kt.domain.entity.QOrderEntity;
import com.kt.domain.entity.QPaymentEntity;
import com.kt.domain.entity.QProductEntity;
import com.kt.domain.entity.QSellerEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefundQueryRepositoryImpl implements RefundQueryRepository {

	private final JPAQueryFactory queryFactory;

	private final QRefundHistoryEntity refundHistory = QRefundHistoryEntity.refundHistoryEntity;
	private final QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
	private final QOrderEntity order = QOrderEntity.orderEntity;
	private final QPaymentEntity payment = QPaymentEntity.paymentEntity;
	private final QProductEntity product = QProductEntity.productEntity;
	private final QSellerEntity seller = QSellerEntity.sellerEntity;

	@Override
	public Page<RefundQueryResponse> findRefundsByMember(
		UUID userId,
		Pageable pageable
	) {
		List<RefundQueryResponse> content =
			queryFactory
				.select(
					Projections.constructor(
						RefundQueryResponse.class,
						refundHistory.id,
						orderProduct.id,
						refundHistory.payment.id,
						refundHistory.refundAmount,
						refundHistory.status,
						refundHistory.requestReason,
						refundHistory.rejectReason,
						refundHistory.sellerId,
						refundHistory.createdAt
					)
				)
				.from(refundHistory)
				.join(refundHistory.orderProduct, orderProduct)
				.join(orderProduct.order, order)
				.join(refundHistory.payment, payment)
				.where(order.orderBy.id.eq(userId))
				.orderBy(refundHistory.createdAt.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		return PageableExecutionUtils.getPage(
			content,
			pageable,
			() -> queryFactory
				.select(refundHistory.count())
				.from(refundHistory)
				.join(refundHistory.orderProduct, orderProduct)
				.join(orderProduct.order, order)
				.where(order.orderBy.id.eq(userId))
				.fetchOne()
		);
	}

	@Override
	public Page<RefundQueryResponse> findRefundsBySeller(
		UUID sellerId,
		Pageable pageable
	) {
		List<RefundQueryResponse> content =
			queryFactory
				.select(
					Projections.constructor(
						RefundQueryResponse.class,
						refundHistory.id,
						orderProduct.id,
						payment.id,
						refundHistory.refundAmount,
						refundHistory.status,
						refundHistory.requestReason,
						refundHistory.rejectReason,
						refundHistory.sellerId,
						refundHistory.createdAt
					)
				)
				.from(refundHistory)
				.join(refundHistory.orderProduct, orderProduct)
				.join(orderProduct.product, product)
				.join(product.seller, seller)
				.join(refundHistory.payment, payment)
				.where(
					refundHistory.status.in(
						RefundStatus.REQUESTED,
						RefundStatus.REJECTED,
						RefundStatus.COMPLETED
					),
					seller.id.eq(sellerId)
				)
				.orderBy(refundHistory.createdAt.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		return PageableExecutionUtils.getPage(
			content,
			pageable,
			() -> queryFactory
				.select(refundHistory.count())
				.from(refundHistory)
				.join(refundHistory.orderProduct, orderProduct)
				.join(orderProduct.product, product)
				.join(product.seller, seller)
				.where(
					refundHistory.status.eq(RefundStatus.REQUESTED),
					seller.id.eq(sellerId)
				)
				.fetchOne()
		);
	}

}
