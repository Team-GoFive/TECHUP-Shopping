package com.kt.api.seller.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.kt.common.CurrentUserCreator;
import com.kt.common.MockMvcTest;
import com.kt.common.SellerEntityCreator;
import com.kt.common.UserEntityCreator;
import com.kt.domain.dto.request.SellerProductRequest;
import com.kt.domain.entity.CategoryEntity;
import com.kt.domain.entity.InventoryEntity;
import com.kt.domain.entity.ProductEntity;
import com.kt.domain.entity.SellerEntity;
import com.kt.domain.entity.UserEntity;
import com.kt.repository.CategoryRepository;
import com.kt.repository.inventory.InventoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.seller.SellerRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.DefaultCurrentUser;

@DisplayName("판매자 상품 수정 - PUT /api/seller/products/{productId}")
public class ProductUpdateTest extends MockMvcTest {

	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	InventoryRepository inventoryRepository;
	@Autowired
	SellerRepository sellerRepository;
	@Autowired
	UserRepository userRepository;

	private SellerEntity testSeller;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private DefaultCurrentUser sellerDetails;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_수정_실패__상품명이_공백이거나_null일_경우_400_BadRequest(String invalidName) throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);

		SellerProductRequest.Update request = new SellerProductRequest.Update(
			invalidName,
			1000L,
			100L,
			testCategory.getId()
		);

		// when
		ResultActions actions = mockMvc.perform(put("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_수정_실패__가격이_null일_경우_400_BadRequest(Long price) throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);

		SellerProductRequest.Update request = new SellerProductRequest.Update(
			"판매자 상품",
			price,
			100L,
			testCategory.getId()
		);

		// when
		ResultActions actions = mockMvc.perform(put("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_수정_실패__재고가_null일_경우_400_BadRequest(Long stock) throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);

		SellerProductRequest.Update request = new SellerProductRequest.Update(
			"판매자 상품",
			1000L,
			stock,
			testCategory.getId()
		);

		// when
		ResultActions actions = mockMvc.perform(put("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@ParameterizedTest
	@NullSource
	void 상품_수정_실패__카테고리_id가_null일_경우_400_BadRequest(UUID categoryId) throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);

		SellerProductRequest.Update request = new SellerProductRequest.Update(
			"판매자 상품",
			1000L,
			100L,
			categoryId
		);

		// when
		ResultActions actions = mockMvc.perform(put("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isBadRequest());
	}

	@Test
	void 상품_수정_성공__200_OK() throws Exception {
		// given
		ProductEntity product = createProduct(testCategory, testSeller);
		productRepository.save(product);
		InventoryEntity inventory = InventoryEntity.create(product.getId(), 1000L);
		inventoryRepository.save(inventory);

		SellerProductRequest.Update request = new SellerProductRequest.Update(
			"수정된 판매자 상품",
			2000L,
			50L,
			testCategory.getId()
		);

		// when
		ResultActions actions = mockMvc.perform(put("/api/seller/products/{productId}", product.getId())
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		ProductEntity updatedProduct = productRepository.findById(product.getId()).orElseThrow();
		InventoryEntity updatedInventory = inventoryRepository.findByProductIdOrThrow(product.getId());
		assertThat(updatedProduct.getName()).isEqualTo(request.name());
		assertThat(updatedProduct.getPrice()).isEqualTo(request.price());
		assertThat(updatedInventory.getStock()).isEqualTo(request.stock());
		assertThat(updatedProduct.getCategory().getId()).isEqualTo(request.categoryId());
	}
}
