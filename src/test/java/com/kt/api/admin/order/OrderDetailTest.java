package com.kt.api.admin.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.kt.common.MockMvcTest;
import com.kt.common.OrderEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.constant.UserRole;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("주문 상세 조회(어드민) - get /api/admin/orders/{orderId}")
public class OrderDetailTest extends MockMvcTest {

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
	void 주문_상세_조회_성공_200() throws Exception {
		// given
		UserEntity user = UserEntityCreator.createMember();

		savedUser = userRepository.save(user);
		OrderEntity order = OrderEntityCreator.createOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		mockMvc.perform(get("/api/admin/orders/{orderId}", order.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));
	}

	// TODO: 에러 익셉션 적용 후 수정
	@Test
	void 주문_상세_조회__실패_404_NotFound() throws Exception {
		// given

		UUID randomOrderId = UUID.randomUUID();
		mockMvc.perform(get("/api/admin/orders/{orderId}", randomOrderId)
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
			).
			andDo(print())
			.andExpectAll(status().isNotFound());
	}

}
