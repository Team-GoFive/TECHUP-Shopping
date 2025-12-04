package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReceiverVO;

public class AdminOrderResponse {

	public record Search(
		UUID orderId,
		String ordererName,
		OrderStatus status,
		java.time.Instant createdAt
	) {
		public static Search from(OrderEntity order) {
			return new Search(
				order.getId(),
				order.getOrderBy().getName(),
				order.getStatus(),
				order.getCreatedAt()
			);
		}
	}

	public record Detail(
		UUID orderId,
		String ordererName,
		OrderStatus status,
		Instant createdAt,
		ReceiverVO receiver,
		List<ProductSummary> products
	) {
		public static Detail from(OrderEntity order, List<OrderProductEntity> orderProducts) {
			return new Detail(
				order.getId(),
				order.getOrderBy().getName(),
				order.getStatus(),
				order.getCreatedAt(),
				order.getReceiverVO(),
				orderProducts.stream()
					.map(ProductSummary::from)
					.toList()
			);
		}

	}

	public record ProductSummary(
		UUID productId,
		String productName,
		Long quantity
	) {
		public static ProductSummary from(OrderProductEntity orderproduct) {
			return new ProductSummary(
				orderproduct.getProduct().getId(),
				orderproduct.getProduct().getName(),
				orderproduct.getQuantity()
			);
		}
	}


}
