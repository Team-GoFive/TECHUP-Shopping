package com.kt.api.seller.product;

import static com.kt.common.CategoryEntityCreator.*;
import static com.kt.common.ProductEntityCreator.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@DisplayName("판매자 상품 다중 품절 처리 - PATCH /api/seller/products/sold-out")
public class ProductSoldOutTest extends MockMvcTest {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private InventoryRepository inventoryRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SellerRepository sellerRepository;
	@Autowired
	private UserRepository userRepository;

	private SellerEntity testSeller;
	private UserEntity testUser;
	private CategoryEntity testCategory;
	private DefaultCurrentUser sellerDetails;

	private List<ProductEntity> products;

	@BeforeEach
	void setUp() {
		testUser = UserEntityCreator.create();
		userRepository.save(testUser);

		testSeller = SellerEntityCreator.createSeller();
		sellerRepository.save(testSeller);

		testCategory = createCategory();
		categoryRepository.save(testCategory);

		sellerDetails = CurrentUserCreator.getSellerUserDetails(testSeller.getId());

		products = new java.util.ArrayList<>();
		for (int i = 0; i < 5; i++) {
			products.add(createProduct(testCategory, testSeller));
		}
		productRepository.saveAll(products);

		List<InventoryEntity> inventories = new java.util.ArrayList<>();
		for (ProductEntity product : products) {
			InventoryEntity inventory = InventoryEntity.create(product.getId(), 10L);
			inventories.add(inventory);
		}
		inventoryRepository.saveAll(inventories);
	}

	@Test
	void 상품_품절_처리_성공__200_OK() throws Exception {
		// given
		SellerProductRequest.SoldOut request = new SellerProductRequest.SoldOut(
			products.stream().map(ProductEntity::getId).toList()
		);

		// when
		ResultActions actions = mockMvc.perform(patch("/api/seller/products/sold-out")
				.with(user(sellerDetails))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		actions.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("ok"));

		products.forEach(product -> {
			InventoryEntity inventory = inventoryRepository.findByProductIdOrThrow(product.getId());
			Assertions.assertEquals(0, inventory.getStock());
		});
	}
}
