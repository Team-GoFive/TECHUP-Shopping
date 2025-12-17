package com.kt.domain.entity;

import static com.kt.constant.OrderProductStatus.*;

import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order_product")
public class OrderProductEntity extends BaseEntity {
	private Long quantity;
	private Long unitPrice;
	private OrderProductStatus status;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	public static OrderProductEntity create(
		Long quantity,
		Long unitPrice,
		OrderProductStatus status,
		OrderEntity order,
		ProductEntity product
	) {
		return new OrderProductEntity(quantity, unitPrice, status, order, product);
	}

	public void updateStatus(OrderProductStatus newStatus) {
		this.status = newStatus;
	}

	public void cancel() {
		this.status = OrderProductStatus.CANCELED;
	}

	public boolean isCancelable() {
		return status != PURCHASE_CONFIRMED
			&& status != SHIPPING_COMPLETED;
	}

	public void assignOrder(OrderEntity order) {
		this.order = order;
	}

	public void forceChangeStatus(OrderProductStatus status) {
		this.status = status;
	}

}
