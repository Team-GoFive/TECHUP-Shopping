package com.kt.api.admin.order;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.constant.OrderStatus;
import com.kt.constant.UserRole;
import com.kt.domain.entity.OrderEntity;
import com.kt.repository.OrderRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("주문 수정(어드민) - Update api/orders/{id}/change-status")
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
		OrderStatus newStatus = OrderStatus.PURCHASE_CONFIRMED;

		// when
		mockMvc.perform(
				put("/api/admin/orders/{id}/change-status", savedOrder.getId())
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(newStatus))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));

		// then
		OrderEntity updatedOrder = orderRepository.findByIdOrThrow(savedOrder.getId());
		assertThat(updatedOrder.getStatus()).isEqualTo(newStatus);
	}

	@ParameterizedTest
	@NullSource
	void 주문_상태변경_실패__상태_null_400_BadRequest(
		OrderStatus newStatus
	) throws Exception {
		mockMvc.perform(
				put("/api/admin/orders/{id}/change-status", savedOrder.getId())
					.contentType(MediaType.APPLICATION_JSON)
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.content(objectMapper.writeValueAsString(newStatus))
			)
			.andDo(print())
			.andExpect(status().isBadRequest());

	}
	//
	// @Test
	// void 주문_상태변경_실패_주문없음_403_NotFount() throws Exception {
	//
	// 	// given
	// 	UUID randomId = UUID.randomUUID();
	//
	// 	// when
	// 	mockMvc.perform(
	// 			put("/api/admin/orders/{id}/change-status", randomId)
	// 				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
	// 				.contentType(MediaType.APPLICATION_JSON)
	// 				.content(objectMapper.writeValueAsString(OrderStatus.SHIPPING_COMPLETED))
	// 		)
	// 		.andDo(print())
	// 		.andExpect(status().isNotFound());
	// }
	//
	// @Test
	// void 주문_상태변경_실패__이미구매확정_400_BadRequest() throws Exception {
	//
	// 	// given
	// 	savedOrder.updateStatus(OrderStatus.PURCHASE_CONFIRMED);
	// 	orderRepository.save(savedOrder);
	//
	// 	mockMvc.perform(
	// 			put("/api/admin/orders/{id}/change-status", savedOrder.getId())
	// 				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
	// 				.contentType(MediaType.APPLICATION_JSON)
	// 				.content(objectMapper.writeValueAsString(OrderStatus.SHIPPING_COMPLETED))
	// 		)
	// 		.andDo(print())
	// 		.andExpect(status().isBadRequest());
	// }
	//
	// @Test
	// void 주문_상태변경_실패__배송중_400_() throws Exception {
	//
	// 	// given
	// 	savedOrder.updateStatus(OrderStatus.SHIPPING);
	// 	orderRepository.save(savedOrder);
	//
	// 	mockMvc.perform(
	// 			put("/api/admin/orders/{id}/change-status", savedOrder.getId())
	// 				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
	// 				.contentType(MediaType.APPLICATION_JSON)
	// 				.content(objectMapper.writeValueAsString(OrderStatus.SHIPPING_COMPLETED))
	// 		)
	// 		.andDo(print())
	// 		.andExpect(status().isBadRequest());
	// }
}
