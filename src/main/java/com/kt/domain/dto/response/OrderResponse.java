package com.kt.domain.dto.response;

import java.util.List;
import java.util.UUID;

import com.kt.domain.entity.OrderProductEntity;

import io.swagger.v3.oas.annotations.media.Schema;

public class OrderResponse {
	@Schema(name = "OrderProductsResponse")
	public record OrderProducts(
		UUID orderId,
		List<OrderProductItem> orderProducts
	) {
		public static OrderProducts of(
			UUID orderId,
			List<OrderProductEntity> orderProductEntities
		) {
			return new OrderProducts(
				orderId,
				orderProductEntities.stream()
					.map(OrderProductItem::of)
					.toList()
			);
		}
	}

	@Schema(name = "OrderProductItemResponse")
	public record OrderProductItem(
		UUID orderProductId,
		UUID productId,
		Long quantity,
		Long unitPrice
	) {
		public static OrderProductItem of(OrderProductEntity orderProductEntity) {
			return new OrderProductItem(
				orderProductEntity.getId(),
				orderProductEntity.getProduct().getId(),
				orderProductEntity.getQuantity(),
				orderProductEntity.getUnitPrice());
		}
	}
}
