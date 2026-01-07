package com.kt.domain.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.kt.constant.AccountRole;
import com.kt.constant.Gender;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ReceiverVO;
import com.querydsl.core.annotations.QueryProjection;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserResponse {
	@Schema(name = "UserSearchResponse")
	public record Search(
		UUID id,
		String name,
		String email,
		AccountRole role,
		Gender gender,
		LocalDate birth,
		String mobile
	) {
		@QueryProjection
		public Search {
		}
	}
	@Schema(name = "UserUserDetailResponse")
	public record UserDetail(
		UUID id,
		String name,
		String email,
		AccountRole role,
		Gender gender,
		LocalDate birth,
		String mobile
	) {
	}
	@Schema(name = "UserOrdersResponse")
	public record Orders(
		UUID userId,
		List<OrderItem> orders
	) {
		public static Orders of(UUID userId, List<OrderEntity> orderEntities) {
			return new Orders(
				userId,
				orderEntities.stream()
					.map(OrderItem::of)
					.toList()
			);
		}
	}
	@Schema(name = "UserOrderItemResponse")
	public record OrderItem(
		UUID orderId,
		ReceiverVO receiver
	) {
		public static OrderItem of(OrderEntity order) {
			return new OrderItem(
				order.getId(),
				order.getReceiverVO()
			);
		}
	}
}
