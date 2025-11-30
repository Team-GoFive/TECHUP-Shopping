package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;
import com.kt.service.ReviewService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주문 목록 조회 - GET /api/orders")
public class OrderSearchTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
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
	@Autowired
	ReviewService reviewService;

	UserEntity testMember;

	ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		// 유저 생성
		testMember = createMember();
		userRepository.save(testMember);

		// 카테고리 생성
		CategoryEntity category = createCategory();
		categoryRepository.save(category);
		// 상품 생성
		testProduct = createProduct(category);
		productRepository.save(testProduct);
		// 주문 생성
		productRepository.save(testProduct);
		for (int i = 0; i < 2; i++) {
			List<OrderRequest.Item> items = List.of(
				new OrderRequest.Item(testProduct.getId(), 1L)
			);
			orderService.createOrder(testMember.getEmail(), items);
		}
	}

	@Test
	void 주문_목록_조회_성공__200_OK() throws Exception {
		ResultActions actions = mockMvc.perform(
			get("/api/orders")
				.with(SecurityMockMvcRequestPostProcessors.user(testMember.getEmail()))
				.param("page", "1")
				.param("size", "10")
		);

		actions.andDo(print());
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.list.length()").value(2));

	}
}
