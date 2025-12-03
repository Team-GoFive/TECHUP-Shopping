package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.constant.OrderStatus;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderProductEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;
import com.kt.service.ReviewService;

@DisplayName("상품 리뷰 조회 - GET /api/products/{productId}/reviews")
public class ProductReviewTest extends MockMvcTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	OrderService orderService;

	@Autowired
	ReviewService reviewService;

	UserEntity testMember;
	CategoryEntity testCategory;
	ProductEntity testProduct;
	AddressEntity address;

	@Autowired
	OrderProductRepository orderProductRepository;

	@Autowired
	OrderRepository orderRepository;

	@BeforeEach
	void setUp() {
		testMember = createMember();
		userRepository.save(testMember);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testProduct = createProduct(testCategory);

		address = addressRepository.save(AddressCreator.createAddress(testMember));

		productRepository.save(testProduct);
		for (int i = 0; i < 3; i++) {
			List<OrderRequest.Item> items = List.of(
				new OrderRequest.Item(testProduct.getId(), 1L)
			);
			orderService.createOrder(testMember.getEmail(), items, address.getId());
		}

		orderRepository.findAll().forEach(order -> order.updateStatus(OrderStatus.PURCHASE_CONFIRMED));

		List<OrderProductEntity> list = orderProductRepository.findAll().stream().toList();
		for (int i = 0; i < 3; i++) {
			OrderProductEntity orderProduct = list.get(i);
			reviewService.create(testMember.getMobile(), orderProduct.getId(), "리뷰 내용: 리뷰" + i);
		}
	}

	@Test
	void 상품_id를_통해_리뷰_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products/{productId}/reviews", testProduct.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(testMember.getEmail()))
		);

		// then
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.length()").value(3));
	}
}
