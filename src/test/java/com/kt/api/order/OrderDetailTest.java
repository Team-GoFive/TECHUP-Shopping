package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static com.kt.common.UserEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.seller.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.OrderEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;

@DisplayName("상품 상세 조회 - GET /api/orders/{orderId}")
public class OrderDetailTest extends MockMvcTest {

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
	AddressRepository addressRepository;
	@Autowired
	SellerRepository sellerRepository;

	UserEntity testMember;

	ProductEntity testProduct;

	AddressEntity testAddress;
	SellerEntity testSeller;

	@BeforeEach
	void setUp() {
		testMember = create();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = createProduct(category, testSeller);
		productRepository.save(testProduct);

		testAddress = addressRepository.save(AddressCreator.createAddress(testMember));

		List<OrderRequest.Item> items = List.of(
			new OrderRequest.Item(testProduct.getId(), 1L)
		);
		OrderRequest orderRequest = new OrderRequest(
			items,
			testAddress.getId()
		);

		orderService.createOrder(
			testMember.getId(),
			orderRequest
		);
	}

	@Test
	void 주문_상세_조회_성공__200_OK() throws Exception {
		// when
		OrderEntity orderEntity = orderRepository.findAll().stream().findFirst().orElseThrow();

		ResultActions actions = mockMvc.perform(
			get("/api/orders/{orderId}", orderEntity.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(testMember.getEmail()))
		);

		// then
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.orderId")
			.value(orderEntity.getId().toString()));
		actions.andExpect(jsonPath("$.data.products.length()").value(1));
		actions.andExpect(jsonPath("$.data.products[0].productId")
			.value(testProduct.getId().toString()));
	}
}
