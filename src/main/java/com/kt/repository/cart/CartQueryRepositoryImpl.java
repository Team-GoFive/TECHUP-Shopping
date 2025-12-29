package com.kt.repository.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kt.constant.ProductStatus;
import com.kt.domain.dto.response.CartItemViewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import static com.kt.domain.entity.QCartEntity.cartEntity;
import static com.kt.domain.entity.QCartItemEntity.cartItemEntity;
import static com.kt.domain.entity.QProductEntity.productEntity;

@Repository
@RequiredArgsConstructor

public class CartQueryRepositoryImpl implements CartQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<CartItemViewResponse> findCartItems(UUID userId) {

		return queryFactory
			.select(Projections.constructor(
				CartItemViewResponse.class,
				cartItemEntity.id,
				productEntity.id,
				productEntity.name,
				productEntity.price,
				cartItemEntity.quantity,
				productEntity.status.eq(ProductStatus.ACTIVATED)
			))
			.from(cartItemEntity)
			.join(cartItemEntity.cart, cartEntity)
			.join(cartItemEntity.product, productEntity)
			.where(cartEntity.user.id.eq(userId))
			.fetch();
	}
}
