package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.common.TestWithMockMvc;
import com.kt.constant.UserRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@MockMvcTest
@DisplayName("상품 상세 조회 - GET /api/products/{productId}")
public class ProductDetailTest extends TestWithMockMvc {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	CategoryEntity testCategory;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@test.com",
		UserRole.MEMBER
	);

	ProductEntity activatedProduct;
	ProductEntity inActivatedProduct;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		activatedProduct = createProduct(testCategory);
		productRepository.save(activatedProduct);

		inActivatedProduct = createProduct(testCategory);
		inActivatedProduct.inActivate();
		productRepository.save(inActivatedProduct);
	}

	@Test
	void 회원_상품_상세_조회_성공__200_OK() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
				get("/api/products/{productId}", activatedProduct.getId())
					.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
			)
			.andExpect(status().isOk());

		actions.andDo(print());
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.id").value(activatedProduct.getId().toString()));
	}

	@Test
	void 존재하지_않는_상품_id_조회_시_404_Not_Found() throws Exception {
		//  when
		// ResultActions actions = mockMvc.perform(
		// 	get("/api/products/{productId}", UUID.randomUUID())
		// 		.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
		// );
		//
		// // then
		// actions.andExpect(status().isNotFound());
		// TODO: 예외 처리 구현 후 수정 필요
	}

	@Test
	void 비활성화된_상품_id로_조회_시_404_Not_Found() throws Exception {
		//  when
		// ResultActions actions = mockMvc.perform(
		// 	get("/api/products/{productId}", inActivatedProduct.getId())
		// 		.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
		// );

		// TODO: 예외 처리 구현 후 수정 필요
	}
}
