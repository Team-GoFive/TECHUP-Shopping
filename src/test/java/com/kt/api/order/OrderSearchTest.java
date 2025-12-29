package com.kt.api.order;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.kt.common.UserEntityCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.seller.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.AddressCreator;
import com.kt.common.MockMvcTest;
import com.kt.domain.dto.request.OrderRequest;
import com.kt.domain.entity.AddressEntity;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.AddressRepository;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.OrderService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("주문 목록 조회 - GET /api/orders")
public class OrderSearchTest extends MockMvcTest {
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderService orderService;
	@Autowired
	UserRepository userRepository;
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
		testMember = UserEntityCreator.create();
		userRepository.save(testMember);

		CategoryEntity category = createCategory();
		categoryRepository.save(category);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = createProduct(category, testSeller);
		productRepository.save(testProduct);

		testAddress = addressRepository.save(AddressCreator.createAddress(testMember));

		for (int i = 0; i < 2; i++) {
			List<OrderRequest.Item> items = List.of(
				new OrderRequest.Item(testProduct.getId(), 1L, testSeller.getId())
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
