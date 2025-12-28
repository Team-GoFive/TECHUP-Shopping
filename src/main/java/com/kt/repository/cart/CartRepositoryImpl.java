package com.kt.repository.cart;
import java.util.Optional;
import java.util.UUID;

import com.kt.domain.entity.CartEntity;
import com.kt.domain.entity.QCartEntity;
import com.kt.domain.entity.QCartItemEntity;
import com.kt.domain.entity.QProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<CartEntity> findCartWithItems(UUID userId) {

		QCartEntity cart = QCartEntity.cartEntity;
		QCartItemEntity cartItem = QCartItemEntity.cartItemEntity;
		QProductEntity product = QProductEntity.productEntity;

		CartEntity result = queryFactory
			.selectFrom(cart)
			.leftJoin(cart.items, cartItem).fetchJoin()
			.leftJoin(cartItem.product, product).fetchJoin()
			.where(cart.user.id.eq(userId))
			.fetchOne();

		return Optional.ofNullable(result);
	}
}
