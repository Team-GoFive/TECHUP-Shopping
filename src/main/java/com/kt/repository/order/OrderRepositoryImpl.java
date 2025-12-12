package com.kt.repository.order;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.QOrderEntity;
import com.kt.domain.entity.QOrderProductEntity;
import com.kt.domain.entity.QProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QOrderEntity order = QOrderEntity.orderEntity;
	private final QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
	private final QProductEntity product = QProductEntity.productEntity;

	@Override
	public Optional<OrderEntity> findDetailWithProducts(UUID orderId) {

		OrderEntity result = jpaQueryFactory
			.select(order)
			.distinct()
			.from(order)
			.leftJoin(order.orderProducts, orderProduct).fetchJoin()
			.leftJoin(orderProduct.product, product).fetchJoin()
			.where(order.id.eq(orderId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}