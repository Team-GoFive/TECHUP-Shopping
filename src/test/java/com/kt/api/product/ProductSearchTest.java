package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.ProductStatus;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;

@DisplayName("상품 목록 조회 - GET /api/products")
public class ProductSearchTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	CategoryEntity testCategory;

	ArrayList<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory));
		}

		// 비활성화 상품 추가
		for (int i = 0; i < 5; i++) {
			ProductEntity product = createProduct(testCategory);
			product.inActivate();
			products.add(product);
		}

		productRepository.saveAll(products);
	}

	@Test
	void 상품_조회_성공__200_OK() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(getMemberUserDetails()))
				.param("page", "1")
				.param("size", "10")
		).andDo(print());

		// then
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
				.with(SecurityMockMvcRequestPostProcessors.user(getMemberUserDetails()))
				.param("page", "0")
				.param("size", "10")
		).andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 올바른_사이즈_파라미터가_아닐경우_400_Bad_Request() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(getMemberUserDetails()))
				.param("page", "1")
				.param("size", "0")
		).andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 사이즈_최대값_초과시_400_Bad_Request() throws Exception {
		// when
		ResultActions actions = mockMvc.perform(
			get("/api/products")
				.with(SecurityMockMvcRequestPostProcessors.user(getMemberUserDetails()))
				.param("page", "1")
				.param("size", "21")
		).andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}
}
