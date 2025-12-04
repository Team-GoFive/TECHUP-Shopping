package com.kt.domain.entity;

import java.time.Instant;

import com.kt.constant.ShippingType;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "shipping_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingDetailEntity extends BaseEntity {

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ShippingType shippingType;

	@Column(nullable = true)
	private Instant arrivedAt;

	@ManyToOne
	@JoinColumn(name = "courier_id", nullable = false)
	private CourierEntity courier;

	@ManyToOne
	@JoinColumn(name = "order_product_id", nullable = false)
	private OrderProductEntity orderProduct;

	protected ShippingDetailEntity(
		CourierEntity courier,
		OrderProductEntity orderProduct
	) {
		this.shippingType = ShippingType.READY;
		this.courier = courier;
		this.orderProduct = orderProduct;
	}

	public static ShippingDetailEntity create(
		final CourierEntity courier,
		final OrderProductEntity orderProduct
	) {
		return new ShippingDetailEntity(courier, orderProduct);
	}

	public void startShipping() {
		this.shippingType = ShippingType.DELIVERING;
	}

	public void returnShipping() {
		this.shippingType = ShippingType.RETURNING;
	}

}
