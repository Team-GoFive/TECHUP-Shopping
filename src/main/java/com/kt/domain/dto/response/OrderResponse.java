package com.kt.domain.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.kt.constant.OrderDerivedStatus;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ReceiverVO;

import io.swagger.v3.oas.annotations.media.Schema;

public class OrderResponse {
	@Schema(name = "OrderProductsResponse")
	public record OrderProducts(
		UUID orderId,
		List<OrderProductItem> orderProducts
	) {
		public static OrderProducts from(
			UUID orderId,
			List<OrderProductEntity> orderProductEntities
		) {
			return new OrderProducts(
				orderId,
				orderProductEntities.stream()
					.map(OrderProductItem::from)
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
		public static OrderProductItem from(OrderProductEntity orderProductEntity) {
			return new OrderProductItem(
				orderProductEntity.getId(),
				orderProductEntity.getProduct().getId(),
				orderProductEntity.getQuantity(),
				orderProductEntity.getUnitPrice());
		}
	}

	@Schema(name = "OrderSearchResponse")
	public record Search(
		UUID orderId,
		UUID ordererId,
		OrderDerivedStatus status,
		Instant createdAt
	) {
		public static OrderResponse.Search from(OrderEntity order) {
			return new OrderResponse.Search(
				order.getId(),
				order.getOrderBy().getId(),
				order.getDerivedStatus(),
				order.getCreatedAt()
			);
		}
	}

	@Schema(name = "OrderDetailResponse")
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
					.map(OrderProductItem::from)
					.toList()
			);
		}
	}

}
