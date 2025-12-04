package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.UserRole;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 수정 (어드민) - PUT /api/admin/products/{productId}")
public class ProductUpdateTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	CategoryEntity testCategory;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@test.com",
		UserRole.ADMIN
	);

	ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testProduct = createProduct(testCategory);
		productRepository.save(testProduct);
	}

	@Test
	void 상품_수정_성공__200_OK() throws Exception {
		//  when
		AdminProductRequest.Update request = new AdminProductRequest.Update(
			"수정된 상품명",
			100L,
			10L,
			testCategory.getId()
		);

		ResultActions actions = mockMvc.perform(
			put("/api/admin/products/{productId}", testProduct.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request))
		);

		// then
		actions.andExpect(status().isOk());
		ProductEntity saved = productRepository.findByIdOrThrow(testProduct.getId());
		assertThat(saved.getName()).isEqualTo(request.name());
	}
}
