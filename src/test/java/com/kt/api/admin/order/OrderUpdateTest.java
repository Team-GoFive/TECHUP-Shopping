package com.kt.api.admin.order;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.OrderStatus;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.OrderProductRequest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.OrderEntity;
import com.kt.repository.order.OrderRepository;
import com.kt.security.DefaultCurrentUser;
// TODO: 2차 스프린트 때 모두 새로운 정책에 맞게 변경 필요.

@DisplayName("주문 상태 강제 변경(어드민) - Update api/orders/{orderid}/change-status")
public class OrderUpdateTest extends MockMvcTest {

	@Autowired
	OrderRepository orderRepository;

	OrderEntity savedOrder;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@example.com",
		UserRole.ADMIN
	);

	@BeforeEach
	void setUp() {

		OrderEntity order = OrderEntityCreator.createOrderEntity();
		savedOrder = orderRepository.save(order);
	}

	@Test
	void 주문_상태_변경_성공_200() throws Exception {

		// given
		OrderProductStatus newStatus = OrderProductStatus.PURCHASE_CONFIRMED;
		var request = new OrderProductRequest.ChangeStatus(
			newStatus
		);
		// when
		ResultActions result = mockMvc.perform(
			patch("/api/admin/orders/{orderId}/change-status", savedOrder.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		result.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));

		OrderEntity updatedOrder = orderRepository.findByIdOrThrow(savedOrder.getId());
	}

	@ParameterizedTest
	@NullSource
	void 주문_상태변경_실패__상태_null_400_BadRequest(
		OrderStatus newStatus
	) throws Exception {
		mockMvc.perform(
				patch("/api/admin/orders/{orderId}/change-status", savedOrder.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.content(objectMapper.writeValueAsString(newStatus))
			)
			.andDo(print())
			.andExpect(status().isBadRequest());

	}

	@Test
	void 주문_상태변경_실패_주문없음_404_NotFound() throws Exception {

		// given
		UUID randomId = UUID.randomUUID();
		var request = new OrderRequest.ChangeStatus(
			OrderProductStatus.SHIPPING_COMPLETED
		);

		// when
		mockMvc.perform(
				patch("/api/admin/orders/{orderId}/change-status", randomId)
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void 주문_상태변경_실패__이미구매확정_400_BadRequest() throws Exception {

		// given
		// savedOrder.updateStatus(OrderStatus.PURCHASE_CONFIRMED);
		orderRepository.save(savedOrder);

		mockMvc.perform(
				patch("/api/admin/orders/{orderId}/change-status", savedOrder.getId())
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(OrderStatus.SHIPPING_COMPLETED))
			)
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	void 주문_상태변경_실패__배송중_400_BadRequest() throws Exception {

		// given
		// savedOrder.updateStatus(OrderStatus.SHIPPING);
		orderRepository.save(savedOrder);

		mockMvc.perform(
				patch("/api/admin/orders/{orderId}/change-status", savedOrder.getId())
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(OrderStatus.SHIPPING_COMPLETED))
			)
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
