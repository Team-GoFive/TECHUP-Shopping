package com.kt.api.admin.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
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
import com.kt.repository.OrderRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("주문 리스트 조회(어드민) - get api/orders")
public class OrderListTest extends MockMvcTest {

	@Autowired
	OrderRepository orderRepository;

	OrderEntity savedOrder;
	UserEntity savedUser;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@example.com",
		UserRole.ADMIN
	);
	@Autowired
	private UserRepository userRepository;

	@AfterEach
	void tearDown() {
		orderRepository.deleteAll();
	}

	@Test
	void 주문_리스트_조회_성공_200() throws Exception {
		// given
		UserEntity user = UserEntityCreator.createMember();

		savedUser = userRepository.save(user);
		OrderEntity order = OrderEntityCreator.craeteOrderEntity(savedUser);
		savedOrder = orderRepository.save(order);

		// when
		mockMvc.perform(
				get("/api/admin/orders").
					with(SecurityMockMvcRequestPostProcessors.user(userDetails))
					.param("page", "1")
					.param("size", "10")
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpectAll(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"))
			.andExpect(jsonPath("$.message").value("성공"));
	}

}
