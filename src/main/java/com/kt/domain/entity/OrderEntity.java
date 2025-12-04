package com.kt.domain.entity;

import static lombok.AccessLevel.*;

import java.util.ArrayList;
import java.util.List;

import com.kt.constant.OrderStatus;
import com.kt.domain.entity.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToMany(mappedBy = "order")
	private List<OrderProductEntity> orderProducts = new ArrayList<>();

	protected OrderEntity(
		ReceiverVO receiverVO,
		UserEntity orderBy,
		OrderStatus status
	) {
		this.receiverVO = receiverVO;
		this.orderBy = orderBy;
		this.status = status;
	}

	public static OrderEntity create(
		final ReceiverVO receiverVO,
		final UserEntity orderBy
	) {
		return new OrderEntity(
			receiverVO,
			orderBy,
			OrderStatus.CREATED
		);
	}

	// 테스트 코드에서 사용
	public static OrderEntity create(
		final ReceiverVO receiverVO,
		final UserEntity orderBy,
		final OrderStatus status
	) {
		return new OrderEntity(
			receiverVO,
			orderBy,
			status
		);
	}

	public void cancel() {
		this.status = OrderStatus.CANCELED;
	}

	public void updateReceiverVO(ReceiverVO receiverVO) {
		this.receiverVO = receiverVO;
	}

	public void updateStatus(OrderStatus newStatus) {
		this.status = newStatus;
	}

	public void addOrderProduct(OrderProductEntity orderProduct) {
		this.orderProducts.add(orderProduct);
	}

}
