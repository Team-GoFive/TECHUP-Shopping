package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;
import com.kt.service.OrderService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주문 생성 - POST /api/orders")
public class OrderCreateTest {
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

	ArrayList<ProductEntity> products = new ArrayList<>();

	UserEntity testMember;
	ProductEntity testProduct1;
	ProductEntity testProduct2;

	@BeforeEach
	void setUp() {
		// 유저 생성
		testMember = createMember();
		userRepository.save(testMember);

		// 카테고리 생성
		CategoryEntity category = createCategory();
		categoryRepository.save(category);
		// 상품 1 생성
		testProduct1 = createProduct(category);
		productRepository.save(testProduct1);
		// 상품 2 생성
		testProduct2 = createProduct(category);
		productRepository.save(testProduct2);

		products.add(testProduct1);
		products.add(testProduct2);
	}

	@Test
	void 주문_생성_성공__200_OK() throws Exception {
		// when
		DefaultCurrentUser currentUser = new DefaultCurrentUser(
			testMember.getId(),
			testMember.getEmail(),
			UserRole.MEMBER
		);

		List<OrderRequest.Item> items = products
			.stream()
			.map(product -> new OrderRequest.Item(product.getId(), 1L))
			.toList();
		OrderRequest request = new OrderRequest(items);

		ResultActions actions = mockMvc.perform(
			post("/api/orders")
				.with(SecurityMockMvcRequestPostProcessors.user(currentUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andDo(print());
		ProductEntity saved = productRepository.findByIdOrThrow(testProduct1.getId());
		assertThat(saved.getStock()).isEqualTo(999L);
		assertThat(orderRepository.count()).isEqualTo(1L);
	}

	@Test
	void 여러_상품에_대하여_주문_생성_성공__200_OK() throws Exception {
		// when
		DefaultCurrentUser currentUser = new DefaultCurrentUser(
			testMember.getId(),
			testMember.getEmail(),
			UserRole.MEMBER
		);

		List<OrderRequest.Item> items =
			List.of(
				new OrderRequest.Item(testProduct1.getId(), 1L),
				new OrderRequest.Item(testProduct2.getId(), 2L)
			);
		OrderRequest request = new OrderRequest(items);

		ResultActions actions = mockMvc.perform(
			post("/api/orders")
				.with(SecurityMockMvcRequestPostProcessors.user(currentUser))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andDo(print());
		ProductEntity saved1 = productRepository.findByIdOrThrow(testProduct1.getId());
		ProductEntity saved2 = productRepository.findByIdOrThrow(testProduct2.getId());
		assertThat(saved1.getStock()).isEqualTo(999L);
		assertThat(saved2.getStock()).isEqualTo(998L);
		assertThat(orderRepository.count()).isEqualTo(1L);
	}

	// TODO: 재고 수량 부족으로 인한 주문 생성 실패 테스트 추가하기 (에러 핸들러 추가후)
}
