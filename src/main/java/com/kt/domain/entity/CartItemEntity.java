package com.kt.domain.entity;

import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "cart_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemEntity extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id", nullable = false)
	private CartEntity cart;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@Column(nullable = false)
	private int quantity;

	protected CartItemEntity(
		CartEntity cart,
		ProductEntity product,
		int quantity
	) {
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
	}

	public static CartItemEntity create(
		CartEntity cart,
		ProductEntity product,
		int quantity
	) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.INVALID_CART_ITEM_QUANTITY);
		}
		return new CartItemEntity(cart, product, quantity);
	}

	public void addQuantity(int amount) {
		changeQuantity(this.quantity + amount);
	}

	private static final int MAX_CART_QUANTITY = 100;

	public void changeQuantity(int quantity) {
		if (quantity <= 0) {
			throw new CustomException(ErrorCode.INVALID_CART_ITEM_QUANTITY);
		}
		if (quantity > MAX_CART_QUANTITY) {
			throw new CustomException(ErrorCode.MAX_CART_ITEM_QUANTITY);
		}
		this.quantity = quantity;
	}
}