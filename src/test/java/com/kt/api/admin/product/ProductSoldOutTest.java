package com.kt.api.admin.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.seller.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.constant.ProductStatus;
import com.kt.constant.AccountRole;
import com.kt.domain.dto.request.AdminProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("상품 다중 품절 처리 (어드민) - POST /api/admin/products/sold-out")
public class ProductSoldOutTest extends MockMvcTest {

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

	ArrayList<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		products = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory, testSeller));
		}
		productRepository.saveAll(products);
	}

	@Test
	void 상품_품절_처리_실패__상품_리스트가_null일_경우_400_BadRequest() throws Exception {
		AdminProductRequest.SoldOut request = new AdminProductRequest.SoldOut(
			null
		);

		ResultActions actions = mockMvc.perform(
			patch("/api/admin/products/sold-out")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 상품_품절_처리_성공__200_OK() throws Exception {
		AdminProductRequest.SoldOut request = new AdminProductRequest.SoldOut(
			products.stream().map(ProductEntity::getId).toList()
		);

		ResultActions actions = mockMvc.perform(
			patch("/api/admin/products/sold-out")
				.with(SecurityMockMvcRequestPostProcessors.user(userDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		);

		actions.andExpect(status().isOk());
		products.forEach(product -> Assertions.assertEquals(ProductStatus.IN_ACTIVATED, product.getStatus()));
	}
}
