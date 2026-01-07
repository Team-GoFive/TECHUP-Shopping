package com.kt.api.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.CurrentUserCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;

@DisplayName("상품 상세 조회 - GET /api/products/{productId}")
public class ProductDetailTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;
	@Autowired
	InventoryRepository inventoryRepository;

	@Autowired
	SellerRepository sellerRepository;

	CategoryEntity testCategory;
	SellerEntity testSeller;

	ProductEntity activatedProduct;
	ProductEntity inActivatedProduct;

	@BeforeEach
	void setUp() {
		testCategory = createCategory();
		categoryRepository.save(testCategory);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		activatedProduct = createProduct(testCategory, testSeller);
		productRepository.save(activatedProduct);
		InventoryEntity inventory1 = InventoryEntity.create(activatedProduct.getId(), 10L);
		inventoryRepository.save(inventory1);

		inActivatedProduct = createProduct(testCategory, testSeller);
		inActivatedProduct.inActivate();
		productRepository.save(inActivatedProduct);
		InventoryEntity inventory2 = InventoryEntity.create(inActivatedProduct.getId(), 10L);
		inventoryRepository.save(inventory2);
	}

	@Test
	void 회원_상품_상세_조회_성공__200_OK() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
			get("/api/products/{productId}", activatedProduct.getId())
				.with(user(getMemberUserDetails()))
		).andDo(print());

		// then
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.data.id").value(activatedProduct.getId().toString()));
	}

	@Test
	void 존재하지_않는_상품_id_조회_시_404_Not_Found() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
			get("/api/products/{productId}", UUID.randomUUID())
				.with(user(getMemberUserDetails()))
		).andDo(print());

		// then
		actions.andExpect(status().isNotFound());
	}

	@Test
	void 비활성화된_상품_id로_조회_시_404_Not_Found() throws Exception {
		//  when
		ResultActions actions = mockMvc.perform(
			get("/api/products/{productId}", inActivatedProduct.getId())
				.with(user(getMemberUserDetails()))
		).andDo(print());

		// then
		actions.andExpect(status().isNotFound());
	}
}
