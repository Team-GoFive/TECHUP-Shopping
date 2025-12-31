package com.kt.domain.entity;

import static com.kt.constant.OrderProductStatus.*;

import com.kt.constant.OrderProductStatus;
import com.kt.constant.message.ErrorCode;
import com.kt.domain.entity.common.BaseEntity;
import com.kt.exception.CustomException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
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
		OrderEntity order,
		ProductEntity product
	) {
		return new OrderProductEntity(quantity, unitPrice, CREATED, order, product);
	}

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

	public void confirmPaidOrderProduct() {
		this.status = SHIPPING_READY;
	}

	public void completeRefund() {
		if (this.status != OrderProductStatus.SHIPPING_COMPLETED) {
			throw new CustomException(ErrorCode.INVALID_FORCE_STATUS_TRANSITION);
		}
		this.status = OrderProductStatus.REFUND_COMPLETED; // TODO: 현재는 환불 승인 순간 환불 즉시 처리되고 바로 환불완료 상태가 된다. (승인==실행) 추후 배송기사 도입시 재설계 필요.
		this.product.addStock(this.quantity);
	}

}
