package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.ProductStatus;
import com.kt.constant.UserRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 목록 조회 - GET /api/products")
public class ProductSearchTest extends MockMvcTest {

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

	ArrayList<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory));
		}

		for (int i = 0; i < 5; i++) {
			ProductEntity product = createProduct(testCategory);
			product.inActivate();
			products.add(product);
		}

		productRepository.saveAll(products);
	}

	@Test
	void 회원은_활성화된_상품만_조회_가능__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "1")
				.param("size", "10")
		);
		// then
		actions.andDo(print());
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.list.length()").value(5));
		actions.andExpect(
			jsonPath("$.data.list[*].status",
				everyItem(is(ProductStatus.ACTIVATED.name()))
			)
		);
	}

	@Test
	void 올바른_페이지_파라미터가_아닐경우_400_Bad_Request() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "0")
				.param("size", "10")
		);
		// then
		actions.andDo(print());
		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 올바른_사이즈_파라미터가_아닐경우_400_Bad_Request() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "1")
				.param("size", "0")
		);
		// then
		actions.andDo(print());
		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 사이즈_최대값_초과시_400_Bad_Request() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.param("page", "1")
				.param("size", "21")
		);
		// then
		actions.andDo(print());
		actions.andExpect(status().isBadRequest());
	}
}
