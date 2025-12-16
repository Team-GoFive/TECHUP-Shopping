package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderDerivedStatus;
import com.kt.constant.OrderProductStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReceiverVO;

import io.swagger.v3.oas.annotations.media.Schema;

public class AdminOrderResponse {

	@Schema(name = "AdminOrderProductItemResponse")
	public record OrderProductItem(
		UUID orderProductId,
		UUID productId,
		String productName,
		Long quantity,
		Long unitPrice,
		OrderProductStatus status
	) {
		public static OrderProductItem of(OrderProductEntity orderProductEntity) {
			return new OrderProductItem(
				orderProductEntity.getId(),
				orderProductEntity.getProduct().getId(),
				orderProductEntity.getProduct().getName(),
				orderProductEntity.getQuantity(),
				orderProductEntity.getUnitPrice(),
				orderProductEntity.getStatus());
		}
	}

	@Schema(name = "AdminOrderSearchResponse")
	public record Search(
		UUID orderId,
		UUID ordererId,
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

	@Schema(name = "AdminOrderDetailResponse")
	public record Detail(
		UUID orderId,
		UUID ordererId,
		Instant createdAt,
		ReceiverVO receiver,
		List<OrderProductItem> products
	) {
		public static Detail from(
			OrderEntity order,
			List<OrderProductEntity> orderProducts
		) {
			return new Detail(
				order.getId(),
				order.getOrderBy().getId(),
				order.getCreatedAt(),
				order.getReceiverVO(),
				orderProducts.stream()
					.map(OrderProductItem::of)
					.toList()
			);
		}
	}

}
