package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.OrderStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;

@DisplayName("주문 취소 - PATCH /api/orders/{orderId}/cancel")
public class OrderCancelTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderService orderService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	OrderRepository orderRepository;

	UserEntity testMember;

	ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testProduct = createProduct(category);
		productRepository.save(testProduct);

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(testProduct.getId(), 1L)
		);
		orderService.createOrder(testMember.getEmail(), items);
	}

	@Test
	void 주문_취소_성공__200_OK() throws Exception {
		// when
		OrderEntity saved = orderRepository.findAll().stream().findFirst().orElseThrow();

		ResultActions actions = mockMvc.perform(
			patch("/api/orders/{orderId}/cancel", saved.getId())
				.with(user(testMember.getEmail()))
		);

		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());
		assertThat(saved.getStatus()).isEqualTo(OrderStatus.CANCELED);
	}

	// TODO: 취소 불가 상태일 경우 취소시 실패 테스트 추가 (에러 헨들링 이후)
}
