package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.ProductStatus;
import com.kt.constant.AccountRole;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 삭제 (어드민) - DELETE /api/admin/products/{productId}")
public class ProductDeleteTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;
	@Autowired
	SellerRepository sellerRepository;

	CategoryEntity testCategory;
	SellerEntity testSeller;

	DefaultCurrentUser userDetails = new DefaultCurrentUser(
		UUID.randomUUID(),
		"test@test.com",
		AccountRole.ADMIN
	);

	ProductEntity testProduct;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testProduct = createProduct(testCategory, testSeller);
		productRepository.save(testProduct);
	}

	@Test
	void 상품_삭제_성공__200_OK() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
			delete("/api/admin/products/{productId}", testProduct.getId())
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
		);

		// then
		actions.andExpect(status().isOk());
		ProductEntity saved = productRepository.findByIdOrThrow(testProduct.getId());
		assertEquals(ProductStatus.DELETED, saved.getStatus());
	}
}
