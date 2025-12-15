package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderDerivedStatus;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReceiverVO;

public class AdminOrderResponse {

	public record Search(
		UUID orderId,
		UUID ordererName,
		OrderDerivedStatus status,
		Instant createdAt
	) {
		public static Search from(OrderEntity order) {
			return new Search(
				order.getId(),
				order.getOrderBy().getId(),
				order.getDerivedStatus(),
				order.getCreatedAt()
			);
		}
	}

	public record Detail(
		UUID orderId,
		UUID ordererId,
		Instant createdAt,
		ReceiverVO receiver,
		List<ProductSummary> products
	) {
		public static Detail from(OrderEntity order, List<OrderProductEntity> orderProducts) {
			return new Detail(
				order.getId(),
				order.getOrderBy().getId(),
				order.getCreatedAt(),
				order.getReceiverVO(),
				orderProducts.stream()
					.map(ProductSummary::from)
					.toList()
			);
		}

	}

	public record ProductSummary(
		UUID orderProductId,
		UUID productId,
		String productName,
		Long quantity,
		OrderProductStatus status
	) {
		public static ProductSummary from(OrderProductEntity orderProduct) {
			return new ProductSummary(
				orderProduct.getId(),
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getStatus()
			);
		}
	}


}
