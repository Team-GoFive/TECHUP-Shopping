package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import com.kt.constant.OrderDerivedStatus;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "\"order\"")
@NoArgsConstructor(access = PROTECTED)
public class OrderEntity extends BaseEntity {

	@Embedded
	@Column(nullable = false)
	private ReceiverVO receiverVO;

	@ManyToOne
	@JoinColumn(name = "order_by")
	private UserEntity orderBy;


	@OneToMany(mappedBy = "order")
	private List<OrderProductEntity> orderProducts = new ArrayList<>();

	protected OrderEntity(
		ReceiverVO receiverVO,
		UserEntity orderBy
	) {
		this.receiverVO = receiverVO;
		this.orderBy = orderBy;
	}

	public static OrderEntity create(
		final ReceiverVO receiverVO,
		final UserEntity orderBy
	) {
		return new OrderEntity(
			receiverVO,
			orderBy
		);
	}

	public void updateReceiverVO(ReceiverVO receiverVO) {
		this.receiverVO = receiverVO;
	}

	public void addOrderProduct(OrderProductEntity orderProduct) {
		this.orderProducts.add(orderProduct);
		orderProduct.assignOrder(this);
	}

	public OrderDerivedStatus getDerivedStatus() {

		if (orderProducts.isEmpty()) {
			return OrderDerivedStatus.CREATED;
		}

		boolean allCanceled = orderProducts.stream()
			.allMatch(orderProduct -> orderProduct.getStatus() == OrderProductStatus.CANCELED);

		if (allCanceled) {
			return OrderDerivedStatus.CANCELED;
		}

		boolean allCompleted = orderProducts.stream()
			.allMatch(orderProduct -> orderProduct.getStatus() == OrderProductStatus.SHIPPING_COMPLETED);

		if (allCompleted) {
			return OrderDerivedStatus.COMPLETED;
		}

		boolean anyShipping = orderProducts.stream()
			.anyMatch(orderProduct -> orderProduct.getStatus() == OrderProductStatus.SHIPPING);

		if (anyShipping) {
			return OrderDerivedStatus.SHIPPING;
		}

		boolean anyPaid = orderProducts.stream()
			.anyMatch(orderProduct -> orderProduct.getStatus() == OrderProductStatus.PAID);

		if (anyPaid) {
			return OrderDerivedStatus.PAID;
		}

		return OrderDerivedStatus.CREATED;
	}
}
