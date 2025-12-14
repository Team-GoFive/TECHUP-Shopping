package com.kt.api.admin.order;

import static com.kt.common.CurrentUserCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.OrderProductStatus;
import com.kt.constant.UserRole;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("주문 취소(어드민) - get api/orders/{orderId}")
public class OrderCancelTest extends MockMvcTest {

	OrderEntity savedOrder;
	UserEntity savedUser;
	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@example.com",
		UserRole.ADMIN
	);

	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserRepository userRepository;

	@Test
	void 주문_취소__성공_200() throws Exception {
		//given
		UserEntity user = UserEntityCreator.createMember();
		savedUser = userRepository.save(user);

		OrderEntity order = OrderEntityCreator.createOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		savedOrder.getOrderProducts()
				.forEach(orderProduct ->
					orderProduct.updateStatus(OrderProductStatus.PAID));

		// when & then
		mockMvc.perform(patch("/api/admin/orders/{orderId}/cancel", order.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(user.getId())))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));
	}

	@Test
	void 주문_취소__실패_NotFound_404() throws Exception {
		//given
		UserEntity user = UserEntityCreator.createMember();
		savedUser = userRepository.save(user);

		OrderEntity order = OrderEntityCreator.createOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		savedOrder.getOrderProducts()
			.forEach(orderProduct ->
				orderProduct.updateStatus(OrderProductStatus.PAID));

		mockMvc.perform(patch("/api/orders/{orderId}/cancel", UUID.randomUUID())
				.with(SecurityMockMvcRequestPostProcessors.user(getAdminUserDetails(user.getId())))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isNotFound());
	}

}
